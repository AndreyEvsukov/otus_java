package ru.otus.crm.model;

import jakarta.annotation.Nonnull;
import java.util.Set;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Table(name = "client")
public record Client(
        @Id Long id,
        @Nonnull String name,
        @MappedCollection(idColumn = "client_id") Address address,
        @MappedCollection(idColumn = "client_id") Set<Phone> phones) {}
