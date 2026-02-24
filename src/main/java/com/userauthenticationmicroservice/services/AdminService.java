package com.userauthenticationmicroservice.services;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.userauthenticationmicroservice.dtos.SortParam;
import com.userauthenticationmicroservice.dtos.SortType;
import com.userauthenticationmicroservice.exceptions.UserNotFoundException;
import com.userauthenticationmicroservice.repositories.UserRepository;
import com.userauthenticationmicroservice.repositories.projections.UserInfo;
import com.userauthenticationmicroservice.repositories.projections.UserSummary;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    
    public Page<UserSummary> getAllUsers(int page, int size, List<SortParam> sortParams){
        
        if(sortParams == null || sortParams.isEmpty()){
            return userRepository.findAllProjectedBy(PageRequest.of(page, size), UserSummary.class);
        }

        Sort sort = null;
        if(sortParams.get(0).getSortType() == SortType.ASC){
            sort = Sort.by(sortParams.get(0).getParamName());
        }else{
            sort = Sort.by(sortParams.get(0).getParamName()).descending();
        }
        for(int i=1;i<sortParams.size();i++){
            if(sortParams.get(i).getSortType() == SortType.ASC){
                sort = sort.and(Sort.by(sortParams.get(i).getParamName()));
            }else{
                sort = sort.and(Sort.by(sortParams.get(i).getParamName()).descending());
            }
        }
        return userRepository.findAllProjectedBy(PageRequest.of(page,size,sort), UserSummary.class);
    }

    public UserInfo getUserInfo(UUID userId){
        UserInfo userInfo = userRepository.findAllProjectedById(userId);
        if(userInfo == null){
            throw new UserNotFoundException();
        }
        return userInfo;
    }
}
