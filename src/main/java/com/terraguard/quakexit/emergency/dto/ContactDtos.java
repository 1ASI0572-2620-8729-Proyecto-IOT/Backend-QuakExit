package com.terraguard.quakexit.emergency.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public final class ContactDtos {
    private ContactDtos() {}
    public record ContactRequest(@NotBlank @Size(max=120) String fullName, @NotBlank @Pattern(regexp="^\\+?[1-9]\\d{7,14}$") String phoneNumber, @Size(max=80) String relationship, boolean notifyBySms, @Min(1) int priority) {}
    public record ContactResponse(Long id, String fullName, String phoneNumber, String relationship, boolean notifyBySms, int priority) {}
    public record ContactList(List<ContactResponse> contacts) {}
}
