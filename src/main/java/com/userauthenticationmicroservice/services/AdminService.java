package com.userauthenticationmicroservice.services;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.userauthenticationmicroservice.dtos.SortParam;
import com.userauthenticationmicroservice.dtos.SortType;
import com.userauthenticationmicroservice.exceptions.SubscriptionNotFoundException;
import com.userauthenticationmicroservice.exceptions.UserNotFoundException;
import com.userauthenticationmicroservice.exceptions.UserSuspendedException;
import com.userauthenticationmicroservice.models.Role;
import com.userauthenticationmicroservice.models.Status;
import com.userauthenticationmicroservice.models.Subscription;
import com.userauthenticationmicroservice.models.SubscriptionType;
import com.userauthenticationmicroservice.models.User;
import com.userauthenticationmicroservice.repositories.RoleRepository;
import com.userauthenticationmicroservice.repositories.UserRepository;
import com.userauthenticationmicroservice.repositories.projections.UserInfo;
import com.userauthenticationmicroservice.repositories.projections.UserSummary;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    
    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public UserInfo getUserInfo(UUID userId){
        UserInfo userInfo = userRepository.findAllProjectedById(userId);
        if(userInfo == null){
            throw new UserNotFoundException();
        }
        return userInfo;
    }

    public void suspendUser(@NonNull UUID userId){
        User user =userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException());
        if(user.getStatus().equals(Status.DELETED)){
            throw new UserNotFoundException();
        }
        user.setStatus(Status.SUSPENDED);
        userRepository.save(user);
    }

    public void softDeleteUser(@NonNull UUID userId){
        User user =userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException());
        user.setStatus(Status.DELETED);
        user.getSubscription().setStatus(Status.DELETED);
        userRepository.save(user);
    }

    public void restoreUser(@NonNull UUID userId){
        User user =userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException());
        user.setStatus(Status.ACTIVE);
        userRepository.save(user);
    }

    public void hardDeleteUser(@NonNull UUID userId){
        User user =userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException());
        if(user==null)
            throw new UserNotFoundException();
        userRepository.delete(user);
    }

    public void updateSubscription(@NonNull UUID userId, String subscriptionType, Instant endDate){
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty() || userOpt.get().getStatus().equals(Status.DELETED)) {
            throw new UserNotFoundException();
        }
        else if(userOpt.get().getStatus().equals(Status.SUSPENDED)){
            throw new UserSuspendedException();
        }
        User user = userOpt.get();
        Subscription subscription = user.getSubscription();
        if(subscription == null){
            throw new SubscriptionNotFoundException();
        }
        subscription.setSubscriptionType(SubscriptionType.valueOf(subscriptionType));
        subscription.setStatus(Status.ACTIVE);
        if(subscriptionType.equals(SubscriptionType.LIFETIME.toString())){
            subscription.setEndDate(null);
        }
        else{
            subscription.setEndDate(endDate);
        }
        userRepository.save(user);
    }

    public void addUserRole(@NonNull UUID userId, String newRole) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty() || userOpt.get().getStatus().equals(Status.DELETED)) {
            throw new UserNotFoundException();
        }
        else if(userOpt.get().getStatus().equals(Status.SUSPENDED)){
            throw new UserSuspendedException();
        }

        User user = userOpt.get();
        Role roleToAdd = roleRepository.findByValue(newRole)
                                            .orElseGet(() -> roleRepository.save(new Role(newRole)));
        user.getRoles().add(roleToAdd);
        userRepository.save(user);
    }

    public void removeUserRole(@NonNull UUID userId, String roleToRemove){
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty() || userOpt.get().getStatus().equals(Status.DELETED)) {
            throw new UserNotFoundException();
        }
        else if(userOpt.get().getStatus().equals(Status.SUSPENDED)){
            throw new UserSuspendedException();
        }

        User user = userOpt.get();
        user.getRoles().removeIf(role -> Objects.equals(role.getValue(), roleToRemove));
        userRepository.save(user);
    }
}
