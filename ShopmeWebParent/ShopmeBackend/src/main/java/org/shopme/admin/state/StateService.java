package org.shopme.admin.state;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shopme.common.entity.State;
import org.shopme.common.util.JpaResult;
import org.shopme.common.util.JpaResultType;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StateService {
    private final StateRepository repository;

    public JpaResult save(String name, String country) {
        repository.save(new State(name, country));

        return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully saved state " + name);
    }

    public JpaResult delete(int id) {
        repository.deleteById(id);
        return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully deleted state " + id);
    }

    public List<State> findAll() {
        return repository.findAll();
    }
}
