package com.interlude.web.controller.chat;

import com.interlude.entity.dto.TokenUserInfoDto;
import com.interlude.entity.vo.ResponseVO;
import com.interlude.web.controller.WebBaseController;
import com.interlude.web.entity.dto.chat.WebChatSearchRequestDto;
import com.interlude.web.entity.vo.chat.WebChatSessionDetailVO;
import com.interlude.web.entity.vo.chat.WebChatSessionVO;
import com.interlude.web.entity.vo.chat.WebChatSearchVO;
import com.interlude.web.service.chat.WebChatService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * AI search APIs.
 */
@RestController
@RequestMapping("/chat")
public class ChatController extends WebBaseController {

    @Resource
    private WebChatService webChatService;

    @PostMapping("/session")
    public ResponseVO<WebChatSearchVO> createSession() {
        return getSuccessResponseVO(webChatService.createSession(getLoginUserId()));
    }

    @GetMapping("/sessions")
    public ResponseVO<List<WebChatSessionVO>> sessions() {
        return getSuccessResponseVO(webChatService.listSessions(getLoginUserId()));
    }

    @GetMapping("/session/{sessionId}")
    public ResponseVO<WebChatSessionDetailVO> detail(@PathVariable("sessionId") String sessionId) {
        return getSuccessResponseVO(webChatService.getSessionDetail(sessionId, getLoginUserId()));
    }

    @PostMapping("/search")
    public ResponseVO<WebChatSearchVO> search(@RequestBody(required = false) WebChatSearchRequestDto request) {
        return getSuccessResponseVO(webChatService.search(request, getLoginUserId()));
    }

    private String getLoginUserId() {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        return tokenUserInfoDto == null ? null : tokenUserInfoDto.getUserId();
    }
}
