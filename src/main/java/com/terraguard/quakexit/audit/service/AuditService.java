package com.terraguard.quakexit.audit.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.terraguard.quakexit.audit.dto.AuditDtos.AuditResponse;
import com.terraguard.quakexit.audit.entity.AuditRecord;
import com.terraguard.quakexit.audit.repository.AuditRecordRepository;
import com.terraguard.quakexit.iam.entity.User;
import java.time.Instant;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class AuditService {
    private final AuditRecordRepository records;
    private final ObjectMapper objectMapper;

    @Transactional
    public void record(User user, String action, String entityType, Long entityId, Long propertyId,
                       Long buildingId, Map<String, Object> detail, String ipAddress) {
        try {
            records.save(AuditRecord.builder().user(user).role(user == null ? null : user.getRole().name())
                .action(action).entityType(entityType).entityId(entityId).propertyId(propertyId)
                .buildingId(buildingId).detailJson(detail == null ? null : objectMapper.writeValueAsString(detail))
                .ipAddress(ipAddress).build());
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo registrar la auditoria", ex);
        }
    }

    @Transactional(readOnly = true)
    public Page<AuditResponse> search(Long userId, String action, Instant from, Instant to,
                                      Long propertyId, Long buildingId, String entityType, Pageable pageable) {
        Specification<AuditRecord> specification = Specification.where(equal("user.id", userId))
            .and(equal("action", action)).and(equal("propertyId", propertyId)).and(equal("buildingId", buildingId))
            .and(equal("entityType", entityType)).and(gte("createdAt", from)).and(lte("createdAt", to));
        return records.findAll(specification, pageable).map(this::response);
    }

    private <T> Specification<AuditRecord> equal(String field, T value) {
        return value == null ? null : (root, query, cb) -> cb.equal(resolve(root, field), value);
    }
    private Specification<AuditRecord> gte(String field, Instant value) {
        return value == null ? null : (root, query, cb) -> cb.greaterThanOrEqualTo(root.<Instant>get(field), value);
    }
    private Specification<AuditRecord> lte(String field, Instant value) {
        return value == null ? null : (root, query, cb) -> cb.lessThanOrEqualTo(root.<Instant>get(field), value);
    }
    private jakarta.persistence.criteria.Path<?> resolve(jakarta.persistence.criteria.Root<AuditRecord> root, String field) {
        jakarta.persistence.criteria.Path<?> path = root;
        for (String part : field.split("\\.")) path = path.get(part);
        return path;
    }
    private AuditResponse response(AuditRecord record) {
        Map<String, Object> detail = Map.of();
        try {
            if (record.getDetailJson() != null) detail = objectMapper.readValue(record.getDetailJson(), new TypeReference<>() {});
        } catch (Exception ignored) {}
        return new AuditResponse(record.getId(), record.getUser() == null ? null : record.getUser().getId(),
            record.getUser() == null ? null : record.getUser().getEmail(), record.getRole(), record.getAction(),
            record.getEntityType(), record.getEntityId(), record.getPropertyId(), record.getBuildingId(), detail,
            record.getIpAddress(), record.getCreatedAt());
    }
}
