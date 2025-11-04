package com.t1.marselmkh;

import com.t1.marselmkh.dto.CardEventDto;
import com.t1.marselmkh.entity.Account;
import com.t1.marselmkh.entity.Card;
import com.t1.marselmkh.entity.Status;
import com.t1.marselmkh.exception.AccountBlockedException;
import com.t1.marselmkh.exception.AccountNotFoundException;
import com.t1.marselmkh.mapper.CardMapper;
import com.t1.marselmkh.repository.AccountRepository;
import com.t1.marselmkh.repository.CardRepository;
import com.t1.marselmkh.service.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardMapper cardMapper;

    @InjectMocks
    private CardService cardService;

    private CardEventDto dto;
    private Card mappedCard;
    private Account activeAccount;

    private static final String CARD_ID = "card-111";
    private static final String PAYMENT_SYSTEM = "VISA";


    @BeforeEach
    void init() {
        dto = new CardEventDto();
        dto.setAccountId(1L);
        dto.setCardId(CARD_ID);
        dto.setPaymentSystem(PAYMENT_SYSTEM);
        dto.setStatus(Status.CLOSED);

        mappedCard = new Card();
        mappedCard.setAccountId(dto.getAccountId());
        mappedCard.setCardId(dto.getCardId());
        mappedCard.setPaymentSystem(dto.getPaymentSystem());

        activeAccount = new Account();
        activeAccount.setId(1L);
        activeAccount.setStatus(Status.ACTIVE);
    }

    @Test
    void givenValidData_whenCreateCard_thenCardSavedWithActiveStatus() {
        when(cardMapper.toEntity(dto)).thenReturn(mappedCard);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(activeAccount));
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> {
            Card c = inv.getArgument(0);
            c.setId(10L);
            return c;
        });

        cardService.createCard(dto);

        ArgumentCaptor<Card> captor = ArgumentCaptor.forClass(Card.class);
        verify(cardMapper).toEntity(dto);
        verify(accountRepository).findById(1L);
        verify(cardRepository).save(captor.capture());

        Card saved = captor.getValue();
        assertEquals(Status.ACTIVE, saved.getStatus());
        assertEquals(1L, saved.getAccountId());
        assertEquals(CARD_ID, saved.getCardId());
    }

    @Test
    void givenAccountNotFound_whenCreateCard_thenThrowsAccountNotFoundException() {
        when(cardMapper.toEntity(dto)).thenReturn(mappedCard);
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> cardService.createCard(dto));

        verify(cardRepository, never()).save(any());
    }

    @Test
    void givenBlockedAccount_whenCreateCard_thenThrowsAccountBlockedException() {
        Account blocked = new Account();
        blocked.setId(1L);
        blocked.setStatus(Status.BLOCKED);

        when(cardMapper.toEntity(dto)).thenReturn(mappedCard);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(blocked));

        assertThrows(AccountBlockedException.class, () -> cardService.createCard(dto));

        verify(cardRepository, never()).save(any());
    }
}
