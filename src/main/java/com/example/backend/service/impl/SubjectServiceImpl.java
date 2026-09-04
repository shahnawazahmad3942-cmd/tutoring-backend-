package com.example.backend.service.impl;
/* Implements subject management. Deletion is a soft delete: the row stays so
existing slots and bookings that reference it remain intact. */

import com.example.backend.dto.subject.SubjectRequest;
import com.example.backend.dto.subject.SubjectResponse;
import com.example.backend.entity.Subject;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.SubjectRepository;
import com.example.backend.service.SubjectService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;

    public SubjectServiceImpl(SubjectRepository subjectRepository){
        this.subjectRepository = subjectRepository;
    }

    private SubjectResponse toResponse(Subject subject){
        return new SubjectResponse(subject.getId(),
                           subject.getName(),
                        subject.getDescription(),
                    subject.isActive());
    }


    @Override
    @Transactional
    public SubjectResponse create(SubjectRequest request){
        Subject subject = new Subject();

        subject.setName(request.name());
        subject.setDescription(request.description());
        subject.setActive(true);

        return toResponse(subjectRepository.save(subject));
    }

    @Override
    @Transactional
    public SubjectResponse update(Long id, SubjectRequest request){
        Subject subject = subjectRepository.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("Subject not found"));

        subject.setName(request.name());
        subject.setDescription(request.description());

        return toResponse(subject);
    }

    @Override
    @Transactional(readOnly = true)
    public SubjectResponse getById(Long id){

        Subject subject = subjectRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Subject not found")
        );

        return toResponse(subject);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectResponse> listActive(){

        return subjectRepository.findByActiveTrue()
                .stream()  // List<Subject> → lazy pipeline
                .map(this::toResponse)// entity → DTO, one per row
                .toList();  // immutable List<SubjectResponse>
    }

    //eager:  [e1,e2,e3] → map → [d1,d2,d3] → copy → result   (2 lists)
   //lazy:   e1→d1, e2→d2, e3→d3 → result                    (1 pass)



    @Override
    @Transactional(readOnly = true)
    public List<SubjectResponse> listAll(){

        return subjectRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }
    

    @Override
    @Transactional
    public void deActivate(Long id){

        Subject subject = subjectRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Subject not found")
        );
        subject.setActive(false);
    }

}
