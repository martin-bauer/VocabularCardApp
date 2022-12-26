package de.martin.vocabularcardapp

data class VocabSheet(
    val firstVocab: String?,
    val secondVocab: String?,
    val thirdVocab: String?,
    val difficultyValue: Int = 0
)

