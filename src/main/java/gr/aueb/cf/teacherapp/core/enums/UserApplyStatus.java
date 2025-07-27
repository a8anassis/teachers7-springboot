package gr.aueb.cf.teacherapp.core.enums;

public enum UserApplyStatus {
    PENDING,       // Better than WAITING (more standard terminology)
    APPROVED,      // More formal than ACCEPTED
    REJECTED,
    CANCELLED     // Added for user-initiated withdrawals
}
