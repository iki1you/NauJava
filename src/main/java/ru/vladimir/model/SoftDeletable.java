package ru.vladimir.model;

import java.time.Instant;

public interface SoftDeletable {
    Instant getDeletedAt();

    void setDeletedAt(Instant deletedAt);
}
