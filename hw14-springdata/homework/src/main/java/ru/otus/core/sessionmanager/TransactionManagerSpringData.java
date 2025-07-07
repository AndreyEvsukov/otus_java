package ru.otus.core.sessionmanager;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionManagerSpringData implements TransactionManager {
    @Override
    @Transactional(readOnly = false)
    public <T> T doInTransaction(TransactionAction<T> action) {
        return action.get();
    }

    @Override
    public <T> T doInReadOnlyTransaction(TransactionAction<T> action) {
        return action.get();
    }
}
