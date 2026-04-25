package com.catalon.guard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catalon.guard.data.local.db.entity.AgentPresetEntity
import com.catalon.guard.data.repository.AgentPresetRepository
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AgentPresetsViewModel @Inject constructor(
    private val repository: AgentPresetRepository,
    private val gson: Gson
) : ViewModel() {

    data class UiState(
        val presets: List<AgentPresetEntity> = emptyList(),
        val editingPreset: AgentPresetEntity? = null,
        val showEditor: Boolean = false,
        val jsonError: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeAll().collect { presets ->
                _uiState.update { it.copy(presets = presets) }
            }
        }
    }

    fun openEditor(preset: AgentPresetEntity?) {
        _uiState.update { it.copy(editingPreset = preset, showEditor = true, jsonError = null) }
    }

    fun closeEditor() {
        _uiState.update { it.copy(editingPreset = null, showEditor = false, jsonError = null) }
    }

    fun createOrUpdate(preset: AgentPresetEntity) {
        val schemaJson = preset.functionSchemaJson
        if (!schemaJson.isNullOrBlank()) {
            try {
                gson.fromJson(schemaJson, JsonObject::class.java)
            } catch (e: Exception) {
                _uiState.update { it.copy(jsonError = "Invalid JSON: ${e.message}") }
                return
            }
        }
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val toSave = if (preset.id.isEmpty())
                preset.copy(id = UUID.randomUUID().toString(), createdAt = now, updatedAt = now)
            else
                preset.copy(updatedAt = now)
            repository.save(toSave)
            closeEditor()
        }
    }

    fun delete(preset: AgentPresetEntity) {
        if (preset.isBuiltIn) return
        viewModelScope.launch { repository.delete(preset) }
    }
}
