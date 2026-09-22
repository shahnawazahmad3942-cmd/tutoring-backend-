package com.example.backend.service.booking;

/* Resolves the BookingPolicy registered for a given booking type. */
import com.example.backend.enums.BookingType;
import com.example.backend.exception.BusinessRuleException;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class BookingPolicyFactory {

    private final Map<BookingType, BookingPolicy> policies = new EnumMap<>(BookingType.class);

    public BookingPolicyFactory(List<BookingPolicy> availablePolicies){
        for(BookingPolicy policy : availablePolicies){
            policies.put(policy.supportedBookingType(), policy);
        }
    }

    public BookingPolicy resolve(BookingType type){
        BookingPolicy policy = policies.get(type);
        if(policy == null){
        throw new BusinessRuleException("Unsupported booking type : "+type);
        }
        return policy;
        
    }

    
    
}
