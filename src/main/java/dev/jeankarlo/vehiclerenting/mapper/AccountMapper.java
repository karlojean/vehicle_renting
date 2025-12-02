package dev.jeankarlo.vehiclerenting.mapper;

import dev.jeankarlo.vehiclerenting.dto.account.AccountCreateDTO;
import dev.jeankarlo.vehiclerenting.dto.account.AccountResponseDTO;
import dev.jeankarlo.vehiclerenting.entity.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    Account toEntity(AccountCreateDTO dto);
    AccountResponseDTO toResponseDTO(Account account);
}
