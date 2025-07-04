package HireCraft.com.SpringBoot.services.impl;

import HireCraft.com.SpringBoot.dtos.requests.MessageRequest;
import HireCraft.com.SpringBoot.dtos.response.MessageResponse;
import HireCraft.com.SpringBoot.models.Booking;
import HireCraft.com.SpringBoot.models.ClientProfile;
import HireCraft.com.SpringBoot.models.Message;
import HireCraft.com.SpringBoot.models.ServiceProviderProfile;
import HireCraft.com.SpringBoot.repository.BookingRepository;
import HireCraft.com.SpringBoot.repository.ClientProfileRepository;
import HireCraft.com.SpringBoot.repository.MessageRepository;
import HireCraft.com.SpringBoot.repository.ServiceProviderProfileRepository;
import HireCraft.com.SpringBoot.services.MessageService;
import HireCraft.com.SpringBoot.utils.EncryptorUtil;
import HireCraft.com.SpringBoot.utils.TimeDateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
//@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final BookingRepository bookingRepository;
    private final ClientProfileRepository clientProfileRepository;
    private final ServiceProviderProfileRepository providerRepository;
    private final EncryptorUtil encryptorUtil;

    public MessageServiceImpl(MessageRepository messageRepository, BookingRepository bookingRepository, ClientProfileRepository clientProfileRepository, ServiceProviderProfileRepository providerRepository, EncryptorUtil encryptorUtil) {
        this.messageRepository = messageRepository;
        this.bookingRepository = bookingRepository;
        this.clientProfileRepository = clientProfileRepository;
        this.providerRepository = providerRepository;
        this.encryptorUtil = encryptorUtil;
    }

    @Override
    public void sendMessageToBooking(MessageRequest request, UserDetails userDetails) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        Message message = new Message();
        message.setBooking(booking);
        message.setEncryptedContent(encryptorUtil.encrypt(request.getContent()));

        ClientProfile clientProfile = clientProfileRepository.findByUserEmail(userDetails.getUsername()).orElse(null);
        ServiceProviderProfile providerProfile = providerRepository.findByUserEmail(userDetails.getUsername()).orElse(null);

        if (clientProfile != null) {
            message.setClientProfile(clientProfile);
        } else if (providerProfile != null) {
            message.setProviderProfile(providerProfile);
        } else {
            throw new RuntimeException("Unauthorized sender");
        }

        messageRepository.save(message);
    }

    @Override
    public List<MessageResponse> getConversation(Long bookingId) {
        return messageRepository.findByBookingIdOrderBySentAtAsc(bookingId)
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MessageResponse sendMessage(MessageRequest request, UserDetails userDetails) {
        sendMessageToBooking(request, userDetails);
        return getConversation(request.getBookingId()).stream()
                .reduce((first, second) -> second).orElse(null);
    }

    @Override
    public List<MessageResponse> getMessagesForBooking(Long bookingId) {
        return List.of();
    }

    private MessageResponse mapToResponse(Message message) {
        MessageResponse response = new MessageResponse();

        if (message.getClientProfile() != null) {
            response.setSenderType("CLIENT");
            response.setSenderFullName(message.getClientProfile().getUser().getFirstName()
                    + " " + message.getClientProfile().getUser().getLastName());
        } else if (message.getProviderProfile() != null) {
            response.setSenderType("PROVIDER");
            response.setSenderFullName(message.getProviderProfile().getUser().getFirstName()
                    + " " + message.getProviderProfile().getUser().getLastName());
        }

        response.setContent(encryptorUtil.decrypt(message.getEncryptedContent()));
        response.setDateStamp(TimeDateUtil.getDateLabel(message.getSentAt()));
        response.setTimeSent(TimeDateUtil.formatTime(message.getSentAt()));

        return response;
    }
}
