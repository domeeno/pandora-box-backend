package com.pandora.courseservice.service

import com.pandora.courseservice.dto.SubjectDTO
import com.pandora.courseservice.dto.SubjectTreeDTO
import com.pandora.courseservice.models.Subject

interface SubjectService {

    fun getAllSubjects(): List<Subject>

    fun getSubjectTree(subjectId: String): SubjectTreeDTO

    fun createSubject(userSubjectId: String, userId: String, dto: SubjectDTO): String

    fun updateSubject(subjectId: String, dto: SubjectDTO): String

    fun deleteSubject(subjectId: String): String
}
