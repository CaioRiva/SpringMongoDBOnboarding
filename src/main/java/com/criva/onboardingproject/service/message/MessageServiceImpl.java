package com.criva.onboardingproject.service.message;

import com.criva.onboardingproject.handler.exception.room.RoomNotFoundException;
import com.criva.onboardingproject.model.dao.MessageDAO;
import com.criva.onboardingproject.model.dto.MessageCreationDTO;
import com.criva.onboardingproject.model.vo.message.ContextVO;
import com.criva.onboardingproject.model.vo.message.MessageVO;
import com.criva.onboardingproject.model.vo.room.RoomVO;
import com.criva.onboardingproject.service.room.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService{

    private RoomService roomService;
    private MessageDAO messageDAO;
    private ContextService contextService;

    @Autowired
    public MessageServiceImpl(RoomService roomService, MessageDAO messageDAO, ContextService contextService) {

        this.roomService = roomService;
        this.messageDAO = messageDAO;
        this.contextService = contextService;
    }

    @Override
    public MessageVO saveMessage(MessageVO message) {

        return messageDAO.save(message);
    }

    @Override
    public void sendMessage(MessageCreationDTO messageCreation) {

        List<String> contextsId = new ArrayList<>();
        RoomVO room = roomService.findRoomByParticipantId(
                messageCreation.getSenderParticipantId());

        if(room == null) {

            throw new RoomNotFoundException();
        }

        List<String> participantsId = room.getParticipantsId();
        participantsId.forEach(id -> contextsId.add(contextService.saveContext(
                new ContextVO(id, Boolean.TRUE, Boolean.FALSE)).getId()));

        saveMessage(new MessageVO(messageCreation.getText(), Instant.now(),
                Boolean.TRUE, messageCreation.getSenderParticipantId(), contextsId));
    }
}