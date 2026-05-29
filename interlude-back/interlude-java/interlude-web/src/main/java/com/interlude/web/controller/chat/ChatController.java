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
 * AI 搜索与会话管理接口。
 */
@RestController
@RequestMapping("/chat")
public class ChatController extends WebBaseController {

    @Resource
    private WebChatService webChatService;

    /**
     * 创建一个新的 AI 会话。
     */
    @PostMapping("/session")
    public ResponseVO<WebChatSearchVO> createSession() {
        return getSuccessResponseVO(webChatService.createSession(getLoginUserId()));
    }

    /**
     * 获取当前用户的会话列表。
     */
    @GetMapping("/sessions")
    public ResponseVO<List<WebChatSessionVO>> sessions() {
        return getSuccessResponseVO(webChatService.listSessions(getLoginUserId()));
    }

    /**
     * 获取指定会话的历史消息明细。
     */
    @GetMapping("/session/{sessionId}")
    public ResponseVO<WebChatSessionDetailVO> detail(@PathVariable("sessionId") String sessionId) {
        return getSuccessResponseVO(webChatService.getSessionDetail(sessionId, getLoginUserId()));
    }

    /**
     * 提交一轮 AI 问答，必要时联动站内搜索结果。
     */
    @PostMapping("/search")
    public ResponseVO<WebChatSearchVO> search(@RequestBody(required = false) WebChatSearchRequestDto request) {
        return getSuccessResponseVO(webChatService.search(request, getLoginUserId()));
    }

    /**
     * 从登录态中提取当前用户 ID。
     */
    private String getLoginUserId() {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        return tokenUserInfoDto == null ? null : tokenUserInfoDto.getUserId();
    }
}
