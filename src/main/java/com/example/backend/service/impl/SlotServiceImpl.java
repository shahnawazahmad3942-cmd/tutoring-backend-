package com.example.backend.service.impl;

/* Implements slot management. Enforces that a subject is active and that a new
slot does not overlap an existing one for the same subject. */

import com.example.backend.dto.slot.SlotRequest;
import com.example.backend.dto.slot.SlotResponse;
import com.example.backend.entity.Slot;
import com.example.backend.entity.Subject;
import com.example.backend.enums.SlotStatus;
import com.example.backend.exception.BusinessRuleException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.SlotRepository;
import com.example.backend.repository.SubjectRepository;
import com.example.backend.service.SlotService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class SlotServiceImpl implements SlotService{

    private final SlotRepository slotRepository;
    private final SubjectRepository subjectRepository;

    public SlotServiceImpl(SlotRepository slotRepository, SubjectRepository subjectRepository) {
        this.slotRepository = slotRepository;
        this.subjectRepository = subjectRepository;
    }

    private SlotResponse toResponse(Slot slot){
        return new SlotResponse(slot.getId(), slot.getSubject().getId(), slot.getSubject().getName(), slot.getStartTime(), slot.getEndTime(), slot.getStatus());
    }

    private void assertNoOverlap(Subject subject, LocalDateTime startTime, LocalDateTime endTime, Long excludeId){

        if(slotRepository.existsOverlapping(subject, startTime, endTime, excludeId)){
            throw new BusinessRuleException("Slot overlaps an existing slot for this subject");
        }

    }


    private Subject findActiveSubject(Long subjectId){

        Subject subject = subjectRepository.findById(subjectId).orElseThrow(
            () -> new ResourceNotFoundException("Subject not found with id " + subjectId)
        );

        if(!subject.isActive()){
            throw new BusinessRuleException("Cannot schedule a slot for an inactive subject");
        }

        return subject;
    }


    @Override
    @Transactional
    public SlotResponse create(SlotRequest request) {
        Subject subject = findActiveSubject(request.subjectId());

        assertNoOverlap(subject, request.startTime(), request.endTime(), null);

        Slot slot = new Slot();
        slot.setSubject(subject);
        slot.setStartTime(request.startTime());
        slot.setEndTime(request.endTime());
        slot.setStatus(SlotStatus.AVAILABLE);

        return toResponse(slotRepository.save(slot));
 
    }

    @Override
    @Transactional
    public SlotResponse update(Long id, SlotRequest request){
    
        Slot slot = slotRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("slot not found")
        );

        if(slot.getStatus() != SlotStatus.AVAILABLE){
            throw new BusinessRuleException("Only an available slot can be modified");
        }

        Subject subject = findActiveSubject(request.subjectId());

        assertNoOverlap(subject, request.startTime(), request.endTime(), id);

        slot.setSubject(subject);
        slot.setStartTime(request.startTime());
        slot.setEndTime(request.endTime());

        return toResponse(slot);
    }

    @Override
    @Transactional(readOnly = true)
    public SlotResponse getById(Long id){
        Slot slot = slotRepository.findByIdWithSubject(id).orElseThrow(
            () -> new ResourceNotFoundException("Slot not found with id " + id)
        );

        return toResponse(slot);

    }

    @Override
    @Transactional(readOnly = true)
    public List<SlotResponse> listAvailable(Long subjectId, LocalDateTime from, LocalDateTime to){

        return slotRepository
               .findBySubjectIdAndStatusAndStartTimeBetween(subjectId, SlotStatus.AVAILABLE, from, to)
               .stream()
               .map(this::toResponse)
               .toList();
    }


    @Override
    @Transactional
    public void cancel(Long id){

        Slot slot = slotRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("slot not found")
        );

        if(slot.getStatus() == SlotStatus.BOOKED){
            throw new BusinessRuleException("A booked slot cannot be cancelled directly");
        }

        slot.setStatus(SlotStatus.CANCELLED);
    }


    
}
