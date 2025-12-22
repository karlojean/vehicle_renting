package dev.jeankarlo.vehiclerenting.service.impl;

import dev.jeankarlo.vehiclerenting.dto.account.AccountCreateDTO;
import dev.jeankarlo.vehiclerenting.dto.account.AccountResponseDTO;
import dev.jeankarlo.vehiclerenting.entity.Account;
import dev.jeankarlo.vehiclerenting.exception.BusinessException;
import dev.jeankarlo.vehiclerenting.mapper.AccountMapper;
import dev.jeankarlo.vehiclerenting.repository.AccountRepository;
import dev.jeankarlo.vehiclerenting.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;

    public AccountServiceImpl(AccountRepository accountRepository, AccountMapper accountMapper, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public AccountResponseDTO create(AccountCreateDTO accountCreateDTO) {
        if(accountRepository.existsByEmail((accountCreateDTO.email()))) {
            throw new BusinessException("Já existe uma conta utilizando este email", HttpStatus.CONFLICT);
        }

        Account account = accountMapper.toEntity(accountCreateDTO);
        account.setPassword(passwordEncoder.encode(accountCreateDTO.password()));

        return accountMapper.toResponseDTO(accountRepository.save(account));
    }

    public AccountResponseDTO getById(Long id) {
        Account account = getEntityById(id);

        return accountMapper.toResponseDTO(account);
    }

    public AccountResponseDTO getByUsername(String username) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("Conta não encontrada", HttpStatus.NOT_FOUND));

        return accountMapper.toResponseDTO(account);
    }

    public Account getEntityById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Conta não encontrada", HttpStatus.NOT_FOUND));
    }
}
