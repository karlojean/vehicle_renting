package dev.jeankarlo.vehiclerenting.mapper;

import dev.jeankarlo.vehiclerenting.dto.account.AccountCreateDTO;
import dev.jeankarlo.vehiclerenting.dto.account.AccountResponseDTO;
import dev.jeankarlo.vehiclerenting.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(source = "email", target = "email", qualifiedByName = "normalizeEmail")
    Account toEntity(AccountCreateDTO dto);

    AccountResponseDTO toResponseDTO(Account account);

    @Named("normalizeEmail")
    default String normalizeEmail(String email) {
        if (email == null)
            return null;
        return email.trim().toLowerCase();
    }
}
