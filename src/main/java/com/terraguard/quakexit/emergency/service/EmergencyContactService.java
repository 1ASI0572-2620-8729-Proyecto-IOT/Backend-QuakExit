package com.terraguard.quakexit.emergency.service;

import com.terraguard.quakexit.common.exception.ApiExceptions.DuplicateResourceException;
import com.terraguard.quakexit.audit.service.AuditService;
import com.terraguard.quakexit.emergency.dto.ContactDtos.*;
import com.terraguard.quakexit.emergency.entity.EmergencyContact;
import com.terraguard.quakexit.emergency.repository.EmergencyContactRepository;
import com.terraguard.quakexit.iam.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class EmergencyContactService {
    private final EmergencyContactRepository contacts; private final AuditService audit;
    @Transactional public ContactResponse add(ContactRequest request, User user) {
        if (contacts.existsByUserIdAndPhoneNumber(user.getId(), request.phoneNumber())) throw new DuplicateResourceException("El contacto ya esta registrado");
        EmergencyContact contact = EmergencyContact.builder().user(user).fullName(request.fullName()).phoneNumber(request.phoneNumber()).relationship(request.relationship()).notifyBySms(request.notifyBySms()).priority(request.priority()).build();
        EmergencyContact saved = contacts.save(contact); audit.record(user, "EMERGENCY_CONTACT_CREATED", "EMERGENCY_CONTACT", saved.getId(), user.getId(), null, null, null); return response(saved);
    }
    @Transactional(readOnly=true) public ContactList list(User user) { return new ContactList(contacts.findByUserIdOrderByPriorityAsc(user.getId()).stream().map(this::response).toList()); }
    private ContactResponse response(EmergencyContact c) { return new ContactResponse(c.getId(), c.getFullName(), c.getPhoneNumber(), c.getRelationship(), c.isNotifyBySms(), c.getPriority()); }
}
