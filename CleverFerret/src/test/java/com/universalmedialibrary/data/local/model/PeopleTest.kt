package com.universalmedialibrary.data.local.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PeopleTest {

    @Test
    fun `People model creates instance with correct properties`() {
        // Arrange
        val personId = 123L
        val name = "John Doe"
        val sortName = "Doe, John"
        
        // Act
        val person = People(personId = personId, name = name, sortName = sortName)
        
        // Assert
        assertThat(person.personId).isEqualTo(personId)
        assertThat(person.name).isEqualTo(name)
        assertThat(person.sortName).isEqualTo(sortName)
    }

    @Test
    fun `People model can be created with null sortName`() {
        // Arrange
        val personId = 456L
        val name = "Jane Smith"
        val sortName = null
        
        // Act
        val person = People(personId = personId, name = name, sortName = sortName)
        
        // Assert
        assertThat(person.personId).isEqualTo(personId)
        assertThat(person.name).isEqualTo(name)
        assertThat(person.sortName).isNull()
    }

    @Test
    fun `People model uses default personId when not specified`() {
        // Arrange
        val name = "Default Person"
        
        // Act
        val person = People(name = name, sortName = null)
        
        // Assert
        assertThat(person.personId).isEqualTo(0L)
        assertThat(person.name).isEqualTo(name)
        assertThat(person.sortName).isNull()
    }

    @Test
    fun `People model supports equality comparison`() {
        // Arrange
        val person1 = People(personId = 1L, name = "Test User", sortName = "User, Test")
        val person2 = People(personId = 1L, name = "Test User", sortName = "User, Test")
        val person3 = People(personId = 2L, name = "Test User", sortName = "User, Test")
        
        // Act & Assert
        assertThat(person1).isEqualTo(person2)
        assertThat(person1).isNotEqualTo(person3)
    }
}