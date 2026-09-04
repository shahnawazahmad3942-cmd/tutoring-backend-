package com.example.backend.service;
/* Contract for subject management: admin CRUD plus public listing. */

import com.example.backend.dto.subject.SubjectRequest;
import com.example.backend.dto.subject.SubjectResponse;

import java.util.List;

public interface SubjectService {

    SubjectResponse create(SubjectRequest request);

    SubjectResponse update(Long id, SubjectRequest request);

    SubjectResponse getById(Long id);

    List<SubjectResponse> listActive();

    List<SubjectResponse> listAll();

    void deActivate(Long id);
}
