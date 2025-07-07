package ru.otus.crm.model;

import jakarta.annotation.Nonnull;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Table(name = "address")
public record Address(@Id Long clientId, @Nonnull String street) {}
