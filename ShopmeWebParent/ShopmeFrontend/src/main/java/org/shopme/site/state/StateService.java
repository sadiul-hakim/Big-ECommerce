package org.shopme.site.state;

import lombok.RequiredArgsConstructor;
import org.shopme.common.entity.State;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StateService {

    private final StateRepository stateRepository;

    public List<State> findAll(){
        return stateRepository.findAll();
    }
}
