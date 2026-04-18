﻿<template>
  <div 
    class="douyin-video-player"
    :class="{ 'with-comment-panel': showCommentPanel }"
    ref="playerContainer"
    @mousemove="showControls"
    @mouseleave="startHideTimer"
    @click="togglePlay"
  >
    <div class="video-stage" :style="videoStageStyle">
      <!-- 视频容器 -->
      <div class="video-container" ref="videoContainer"></div>

      <!-- 弹幕图层 -->
      <DanmuLayer
        v-if="showDanmu && !isDanmuDisabledByInteraction"
        :danmu-list="danmuList"
        :current-time="currentTime"
        :is-playing="isPlaying"
        :font-size="danmuFontSize"
        :opacity="danmuOpacity"
        :speed-multiplier="danmuSpeed"
        :area="danmuArea"
        @send-danmu="handleSendDanmu"
      />

      <!-- 中心播放按钮（暂停时可见） -->
      <div class="center-play-button" v-if="!isPlaying && !loading && !error" @click.stop="togglePlay">
        <svg class="play-icon" viewBox="0 0 24 24">
          <path d="M8 5v14l11-7z" />
        </svg>
      </div>


      <!-- 错误遮罩 -->
      <div v-if="error" class="error-overlay">
        <div class="error-content">
          <svg class="error-icon" viewBox="0 0 24 24">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
          </svg>
          <p class="error-text">{{ error }}</p>
          <button class="retry-btn" @click.stop="retry">重试</button>
        </div>
      </div>
    </div>

    <!-- 右侧操作区域 -->
    <div class="sidebar-actions" v-if="controlsVisible">
      <div class="avatar-follow-wrap">
        <!-- 用户头像 -->
        <div class="user-avatar" @click.stop="openAuthorProfile">
          <img
            :src="authorAvatarUrl"
            :alt="`${videoData.author || '用户'}头像`"
            @error="handleAvatarLoadError"
          />
        </div>

        <button
          v-if="followActionState !== 'hidden'"
          class="follow-trigger"
          :class="{
            checked: followActionState === 'check',
            loading: followMutating,
          }"
          :disabled="followMutating"
          @click.stop="handleFollow"
        >
          <span class="follow-icon" aria-hidden="true">
            <svg v-if="followActionState === 'check'" class="follow-svg check" viewBox="0 0 24 24">
              <path d="M5 12.5l4.2 4.2L19 7" />
            </svg>
            <svg v-else class="follow-svg plus" viewBox="0 0 24 24">
              <path d="M12 5.5v13M5.5 12h13" />
            </svg>
          </span>
        </button>
      </div>
      
      <!-- 互动按钮 -->
      <div class="action-buttons">
        <button
          class="action-btn like-btn"
          :class="{ active: isLiked }"
          :disabled="reactionMutating"
          @click.stop="handleLike"
        >
          <svg class="action-icon" viewBox="0 0 24 24">
            <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
          </svg>
          <span class="action-count">{{ likeCount }}</span>
        </button>
        
        <button class="action-btn comment-btn" @click.stop="handleComment">
          <svg class="action-icon" viewBox="0 0 24 24">
            <path d="M21.99 4c0-1.1-.89-2-1.99-2H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h14l4 4-.01-18zM18 14H6v-2h12v2zm0-3H6V9h12v2zm0-3H6V6h12v2z"/>
          </svg>
          <span class="action-count">{{ commentCount }}</span>
        </button>
        
        <button
          class="action-btn share-btn"
          :class="{ active: isShared }"
          :disabled="reactionMutating"
          @click.stop="handleShare"
        >
          <svg class="action-icon" viewBox="0 0 24 24">
            <path d="M18 16.08c-.76 0-1.44.3-1.96.77L8.91 12.7c.05-.23.09-.46.09-.7s-.04-.47-.09-.7l7.05-4.11c.54.5 1.25.81 2.04.81 1.66 0 3-1.34 3-3s-1.34-3-3-3-3 1.34-3 3c0 .24.04.47.09.7L8.04 9.81C7.5 9.31 6.79 9 6 9c-1.66 0-3 1.34-3 3s1.34 3 3 3c.79 0 1.5-.31 2.04-.81l7.12 4.16c-.05.21-.08.43-.08.65 0 1.61 1.31 2.92 2.92 2.92 1.61 0 2.92-1.31 2.92-2.92s-1.31-2.92-2.92-2.92z"/>
          </svg>
          <span class="action-count">{{ shareCount }}</span>
        </button>
        
        <button
          class="action-btn collect-btn"
          :class="{ active: isCollected }"
          :disabled="reactionMutating"
          @click.stop="handleCollect"
        >
          <svg class="action-icon" viewBox="0 0 24 24">
            <path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z"/>
          </svg>
          <span class="action-count">{{ collectCount }}</span>
        </button>
      </div>
    </div>

    <div v-if="showCommentPanel" class="comment-panel" @click.stop>
      <div class="comment-panel-header">
        <span>评论 {{ commentCount }}</span>
        <button class="comment-close-btn" @click.stop="closeCommentPanel">×</button>
      </div>

      <div class="comment-panel-body">
        <div v-if="commentLoading && commentList.length === 0" class="comment-loading">加载中...</div>
        <template v-else>
          <div v-if="commentList.length === 0" class="comment-empty">暂无评论，来抢沙发吧</div>
          <div v-else class="comment-list">
            <div v-for="group in commentGroups" :key="group.root.commentId" class="comment-thread">
              <div class="comment-item">
                <img
                  class="comment-avatar"
                  :src="resolveCommentAvatar(group.root.authorAvatar, group.root.authorId)"
                  :alt="`${group.root.authorName || '用户'}头像`"
                />
                <div class="comment-main">
                  <div class="comment-author-row">
                    <div class="comment-author">{{ group.root.authorName || '匿名用户' }}</div>
                    <span v-if="isAuthorComment(group.root.authorId)" class="comment-author-tag">作者</span>
                  </div>
                  <div class="comment-content">{{ group.root.content }}</div>
                  <div class="comment-meta">
                    <span>{{ formatCommentTime(group.root.createdAt) }}</span>
                    <button
                      class="comment-like-action"
                      :class="{ active: isCommentLiked(group.root.commentId) }"
                      :disabled="isCommentLikeMutating(group.root.commentId)"
                      @click.stop="handleCommentLike(group.root)"
                    >
                      <svg viewBox="0 0 24 24" aria-hidden="true">
                        <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54z"/>
                      </svg>
                      <span>{{ group.root.likeCount || 0 }}</span>
                    </button>
                    <button
                      class="comment-dislike-action"
                      :class="{ active: isCommentDisliked(group.root.commentId) }"
                      @click.stop="handleCommentDislike(group.root)"
                      aria-label="不喜欢"
                    >
                      <svg viewBox="0 0 24 24" aria-hidden="true">
                        <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
                        <path d="M11.9 7.2 10.4 10l2.1 1.2-1.7 2.9"/>
                      </svg>
                      <span>{{ getCommentDislikeCount(group.root) }}</span>
                    </button>
                    <button class="comment-reply-action" @click.stop="beginReplyComment(group.root)">回复</button>
                  </div>
                </div>
              </div>
              <div v-if="group.replies.length > 0" class="comment-replies-wrap">
                <button
                  v-if="!isReplyExpanded(group.root.commentId)"
                  class="comment-replies-toggle"
                  @click.stop="toggleReplyExpanded(group.root.commentId, group.replies.length)"
                >
                  <span>展开{{ group.replies.length }}条回复</span>
                  <svg viewBox="0 0 24 24" aria-hidden="true">
                    <path d="M7 10l5 5 5-5" />
                  </svg>
                </button>
                <template v-else>
                  <div class="comment-reply-list">
                    <div v-for="reply in getVisibleReplies(group)" :key="reply.commentId" class="comment-item is-reply">
                      <img
                        class="comment-avatar is-reply"
                        :src="resolveCommentAvatar(reply.authorAvatar, reply.authorId)"
                        :alt="`${reply.authorName || '用户'}头像`"
                      />
                      <div class="comment-main">
                        <div class="comment-author-row">
                          <div class="comment-author">
                            <template v-if="reply.replyToUserName">
                              <span>{{ reply.authorName || '匿名用户' }}</span>
                              <svg class="comment-reply-arrow-icon" viewBox="0 0 24 24" aria-hidden="true">
                                <path d="M8 5l10 7-10 7z" />
                              </svg>
                              <span class="comment-reply-to">{{ reply.replyToUserName }}</span>
                            </template>
                            <template v-else>
                              {{ reply.authorName || '匿名用户' }}
                            </template>
                          </div>
                          <span v-if="isAuthorComment(reply.authorId)" class="comment-author-tag">作者</span>
                        </div>
                        <div class="comment-content">{{ reply.content }}</div>
                        <div class="comment-meta">
                          <span>{{ formatCommentTime(reply.createdAt) }}</span>
                          <button
                            class="comment-like-action"
                            :class="{ active: isCommentLiked(reply.commentId) }"
                            :disabled="isCommentLikeMutating(reply.commentId)"
                            @click.stop="handleCommentLike(reply)"
                          >
                            <svg viewBox="0 0 24 24" aria-hidden="true">
                              <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54z"/>
                            </svg>
                            <span>{{ reply.likeCount || 0 }}</span>
                          </button>
                          <button
                            class="comment-dislike-action"
                            :class="{ active: isCommentDisliked(reply.commentId) }"
                            @click.stop="handleCommentDislike(reply)"
                            aria-label="不喜欢"
                          >
                            <svg viewBox="0 0 24 24" aria-hidden="true">
                              <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
                              <path d="M11.9 7.2 10.4 10l2.1 1.2-1.7 2.9"/>
                            </svg>
                            <span>{{ getCommentDislikeCount(reply) }}</span>
                          </button>
                          <button class="comment-reply-action" @click.stop="beginReplyComment(reply)">回复</button>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="comment-replies-actions">
                    <button
                      v-if="canReplyExpandMore(group)"
                      class="comment-replies-action-btn"
                      @click.stop="expandMoreReplies(group.root.commentId, group.replies.length)"
                    >
                      <span>展开更多</span>
                      <svg viewBox="0 0 24 24" aria-hidden="true">
                        <path d="M7 10l5 5 5-5" />
                      </svg>
                    </button>
                    <button
                      class="comment-replies-action-btn"
                      @click.stop="collapseReplies(group.root.commentId)"
                    >
                      <span>收起</span>
                      <svg viewBox="0 0 24 24" aria-hidden="true">
                        <path d="M7 14l5-5 5 5" />
                      </svg>
                    </button>
                  </div>
                </template>
              </div>
            </div>
            <button
              v-if="commentHasMore"
              class="comment-load-more"
              :disabled="commentLoading"
              @click.stop="loadMoreComments"
            >
              {{ commentLoading ? '加载中...' : '加载更多' }}
            </button>
          </div>
        </template>
      </div>

      <div class="comment-panel-input">
        <div v-if="commentReplyTarget" class="comment-reply-target">
          <span class="comment-reply-target-text">回复 {{ commentReplyTarget.authorName || '用户' }}</span>
          <button class="comment-reply-cancel" @click.stop="cancelReplyComment">取消</button>
        </div>
        <textarea
          v-model="commentInputText"
          maxlength="500"
          :placeholder="commentReplyTarget ? `回复 ${commentReplyTarget.authorName || '用户'}...` : '说点什么...'"
          @keydown="handleCommentInputKeydown"
          @click.stop
        />
        <button
          class="comment-send-btn"
          :disabled="commentSubmitting || !commentInputText.trim()"
          @click.stop="submitComment"
        >
          {{ commentSubmitting ? '发布中...' : '发布' }}
        </button>
      </div>
    </div>

    <!-- 左侧视频信息区域 -->
    <div class="video-info" v-if="controlsVisible && videoData.author">
      <div class="author-info">
        <span class="author-name" @click.stop="openAuthorProfile">@{{ videoData.author }}</span>
        <span class="publish-time">{{ formatPublishTime(videoData.createTime) }}</span>
      </div>
      <div class="video-description" v-if="videoData.description">
        {{ videoData.description }}
      </div>
    </div>

    <!-- 底部控制条 -->
    <div class="video-control-bar" v-if="controlsVisible" ref="controlBarRef">
      <!-- 第一行：仅显示进度条 -->
      <div class="progress-row">
        <div class="progress-bar" ref="progressBar" @click.stop="seekToPosition">
          <div class="progress-bg"></div>
          <div class="progress-filled" :style="{ width: progressPercent + '%' }"></div>
          <div class="progress-thumb" :style="{ left: progressPercent + '%' }"></div>
        </div>
      </div>

      <!-- 第二行：控制按钮区域 -->
      <div class="control-row">
        <!-- 左侧：播放/暂停与时间信息 -->
        <div class="control-left">
          <!-- 播放/暂停按钮 -->
          <button class="control-btn play-pause-btn" @click.stop="togglePlay">
            <svg v-if="isPlaying" class="pause-icon" viewBox="0 0 24 24">
              <path d="M6 19h4V5H6v14zm8-14v14h4V5h-4z" />
            </svg>
            <svg v-else class="play-icon" viewBox="0 0 24 24">
              <path d="M8 5v14l11-7z" />
            </svg>
          </button>

          <!-- 时间显示 -->
          <div class="time-display">
            <span class="current-time">{{ formatTime(currentTime) }}</span>
            <span class="time-separator">/</span>
            <span class="duration">{{ formatTime(totalDuration) }}</span>
          </div>
        </div>

        <!-- 中间：弹幕控制 -->
        <div class="control-center">
          <!-- 弹幕开关 -->
          <div v-if="!isDanmuDisabledByInteraction" class="danmu-switch-container">
            <span class="switch-label">弹幕</span>
            <ElSwitch
              v-model="showDanmu"
              :active-value="true"
              :inactive-value="false"
              @change="showControls"
              @click.native.stop
              size="small"
              style="--el-switch-on-color: #ff2d55; --el-switch-off-color: #666"
            />
          </div>

          <!-- 弹幕设置 -->
          <div v-if="!isDanmuDisabledByInteraction" class="danmu-settings" ref="danmuSettingsRef">
            <button class="control-btn settings-btn" @click.stop="toggleDanmuSettings">
              <svg class="settings-icon" viewBox="0 0 24 24">
                <path d="M19.14 12.94c.04-.3.06-.61.06-.94 0-.32-.02-.64-.07-.94l2.03-1.58c.18-.14.23-.41.12-.61l-1.92-3.32c-.12-.22-.37-.29-.59-.22l-2.39.96c-.5-.38-1.03-.7-1.62-.94l-.36-2.54c-.04-.24-.24-.41-.48-.41h-3.84c-.24 0-.43.17-.47.41l-.36 2.54c-.59.24-1.13.57-1.62.94l-2.39-.96c-.22-.08-.47 0-.59.22L2.74 8.87c-.12.21-.08.47.12.61l2.03 1.58c-.05.3-.09.63-.09.94s.02.64.07.94l-2.03 1.58c-.18.14-.23.41-.12.61l1.92 3.32c.12.22.37.29.59.22l2.39-.96c.5.38 1.03.7 1.62.94l.36 2.54c.05.24.24.41.48.41h3.84c.24 0 .44-.17.47-.41l.36-2.54c.59-.24 1.13-.56 1.62-.94l2.39.96c.22.08.47 0 .59-.22l1.92-3.32c.12-.22.07-.47-.12-.61l-2.01-1.58zM12 15.6c-1.98 0-3.6-1.62-3.6-3.6s1.62-3.6 3.6-3.6 3.6 1.62 3.6 3.6-1.62 3.6-3.6 3.6z"/>
              </svg>
              <span class="btn-text">弹幕设置</span>
            </button>

            <!-- 弹幕设置菜单 -->
            <div v-if="showDanmuSettings" class="danmu-settings-menu">
              <div class="settings-header">
                <span>弹幕设置</span>
                <button class="close-btn" @click.stop="closeDanmuSettings">
                  <svg class="close-icon" viewBox="0 0 24 24">
                    <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
                  </svg>
                </button>
              </div>
              <div class="settings-content">
                <div class="setting-item">
                  <label>字体大小</label>
                  <div class="slider-container">
                    <input 
                      type="range" 
                      min="12" 
                      max="42" 
                      step="2" 
                      v-model="danmuFontSize"
                      @input="updateDanmuSettings"
                    />
                    <span class="slider-value">{{ danmuFontSize }}px</span>
                  </div>
                </div>
                <div class="setting-item">
                  <label>弹幕透明度</label>
                  <div class="slider-container">
                    <input 
                      type="range" 
                      min="0.2" 
                      max="1" 
                      step="0.1" 
                      v-model="danmuOpacity"
                      @input="updateDanmuSettings"
                    />
                    <span class="slider-value">{{ Math.round(danmuOpacity * 100) }}%</span>
                  </div>
                </div>
                <div class="setting-item">
                  <label>弹幕速度</label>
                  <div class="slider-container">
                    <input 
                      type="range" 
                      min="0.5" 
                      max="2" 
                      step="0.1" 
                      v-model="danmuSpeed"
                      @input="updateDanmuSettings"
                    />
                    <span class="slider-value">{{ danmuSpeed }}x</span>
                  </div>
                </div>
                <div class="setting-item">
                  <label>显示区域</label>
                  <div class="radio-group">
                    <label class="radio-label">
                      <input 
                        type="radio" 
                        value="full" 
                        v-model="danmuArea"
                        @change="updateDanmuSettings"
                      />
                      全屏
                    </label>
                    <label class="radio-label">
                      <input 
                        type="radio" 
                        value="top" 
                        v-model="danmuArea"
                        @change="updateDanmuSettings"
                      />
                      顶部
                    </label>
                    <label class="radio-label">
                      <input 
                        type="radio" 
                        value="bottom" 
                        v-model="danmuArea"
                        @change="updateDanmuSettings"
                      />
                      底部
                    </label>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 弹幕输入框（位于设置右侧） -->
          <div v-if="!isDanmuDisabledByInteraction" class="danmu-input-area">
            <div class="danmu-input-wrapper">
              <input
                ref="danmuInput"
                v-model="danmuInputText"
                type="text"
                placeholder="发个弹幕，开心一下~"
                maxlength="50"
                @keyup.enter="sendDanmuFromInput"
                @focus="showControls"
              />
              <button 
                class="send-danmu-btn" 
                @click.stop="sendDanmuFromInput"
                :disabled="!danmuInputText.trim()"
              >
                <svg class="send-icon" viewBox="0 0 24 24">
                  <path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"/>
                </svg>
              </button>
            </div>
          </div>
        </div>

        <!-- 右侧：其他控制项 -->
        <div class="control-right">
          <!-- 播放速率 -->
          <div class="playback-rate-selector">
            <button class="control-btn" @click.stop="togglePlaybackRateMenu">
              <svg class="speed-icon" viewBox="0 0 24 24">
                <path d="M13.5 4.5c0 1.1-.9 2-2 2s-2-.9-2-2 .9-2 2-2 2 .9 2 2zM10.5 22c-.55 0-1-.45-1-1v-6.14c-.31.11-.65.17-1 .17-1.1 0-2-.9-2-2V9c0-1.1.9-2 2-2 .35 0 .69.06 1 .17V5c0-1.1.9-2 2-2s2 .9 2 2v2.17c.31-.11.65-.17 1-.17 1.1 0 2 .9 2 2v4c0 1.1-.9 2-2 2-.35 0-.69-.06-1-.17V21c0 .55-.45 1-1 1s-1-.45-1-1v-6.14c-.31.11-.65.17-1 .17-1.1 0-2-.9-2-2V9c0-1.1.9-2 2-2 .35 0 .69.06 1 .17V21c0 .55-.45 1-1 1z"/>
              </svg>
              <span class="btn-text">{{ currentPlaybackRate }}x</span>
            </button>

            <!-- 播放速率菜单 -->
            <div v-if="showPlaybackRateMenu" class="dropdown-menu rate-menu">
              <div
                v-for="rate in playbackRates"
                :key="rate"
                class="dropdown-item"
                :class="{ active: currentPlaybackRate === rate }"
                @click="changePlaybackRate(rate)"
              >
                {{ rate }}x
              </div>
            </div>
          </div>

          <!-- 清晰度选择 -->
          <QualitySelector
            :qualities="sortedQualities"
            :current-quality="currentQuality"
            @change-quality="changeQuality"
          />

          <!-- 音量控制 -->
          <div class="volume-control">
            <button class="control-btn" @click.stop="toggleMute">
              <svg v-if="isMuted || volume === 0" class="volume-off-icon" viewBox="0 0 24 24">
                <path d="M16.5 12c0-1.77-1.02-3.29-2.5-4.03v2.21l2.45 2.45c.03-.2.05-.41.05-.63zm2.5 0c0 .94-.2 1.82-.54 2.64l1.51 1.51C20.63 14.91 21 13.5 21 12c0-4.28-2.99-7.86-7-8.77v2.06c2.89.86 5 3.54 5 6.71zM4.27 3L3 4.27 7.73 9H3v6h4l5 5v-6.73l4.25 4.25c-.67.52-1.42.93-2.25 1.18v2.06c1.38-.31 2.63-.95 3.69-1.81L19.73 21 21 19.73l-9-9L4.27 3zM12 4L9.91 6.09 12 8.18V4z"/>
              </svg>
              <svg v-else-if="volume <= 0.5" class="volume-low-icon" viewBox="0 0 24 24">
                <path d="M18.5 12c0-1.77-1.02-3.29-2.5-4.03v8.05c1.48-.73 2.5-2.25 2.5-4.02zM5 9v6h4l5 5V4L9 9H5z"/>
              </svg>
              <svg v-else class="volume-high-icon" viewBox="0 0 24 24">
                <path d="M3 9v6h4l5 5V4L7 9H3zm13.5 3c0-1.77-1.02-3.29-2.5-4.03v8.05c1.48-.73 2.5-2.25 2.5-4.02zM14 3.23v2.06c2.89.86 5 3.54 5 6.71s-2.11 5.85-5 6.71v2.06c4.01-.91 7-4.49 7-8.77s-2.99-7.86-7-8.77z"/>
              </svg>
              <span class="btn-text">{{ Math.round(volume * 100) }}%</span>
            </button>

            <!-- 垂直音量条 -->
            <div class="volume-slider-container vertical">
              <div class="volume-slider vertical" ref="volumeSlider" @click.stop="changeVolumeVertical">
                <div class="volume-track vertical"></div>
                <div
                  class="volume-level vertical"
                  :style="{ height: (isMuted ? 0 : volume) * 100 + '%' }"
                ></div>
                <div
                  class="volume-thumb vertical"
                  :style="{ bottom: (isMuted ? 0 : volume) * 100 + '%' }"
                ></div>
              </div>
            </div>
          </div>

          <!-- 全屏按钮 -->
          <button class="control-btn" @click.stop="toggleFullscreen">
            <svg v-if="isFullscreen" class="fullscreen-exit-icon" viewBox="0 0 24 24">
              <path d="M5 16h3v3h2v-5H5v2zm3-8H5v2h5V5H8v3zm6 11h2v-3h3v-2h-5v5zm2-11V5h-2v5h5V8h-3z"/>
            </svg>
            <svg v-else class="fullscreen-icon" viewBox="0 0 24 24">
              <path d="M7 14H5v5h5v-2H7v-3zm-2-4h2V7h3V5H5v5zm12 7h-3v2h5v-5h-2v3zM14 5v2h3v3h2V5h-5z"/>
            </svg>
            <span class="btn-text">{{ isFullscreen ? '退出全屏' : '全屏' }}</span>
          </button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElSwitch } from 'element-plus'
import 'element-plus/dist/index.css'
import DanmuLayer from './DanmuLayer.vue'
import QualitySelector from './QualitySelector.vue'
import { useDouyinVideoPlayer } from './composables/useDouyinVideoPlayer'
import type { DouyinVideoData } from './composables/useDouyinVideoPlayer'
import type { VideoCommentItem } from '@/api/video'

const props = withDefaults(defineProps<{
  videoData: DouyinVideoData
  autoplay?: boolean
}>(), {
  autoplay: true,
})
const router = useRouter()

const normalizeUserId = (value: unknown): string => {
  if (value === null || value === undefined) {
    return ''
  }
  return String(value).trim()
}

const isAuthorComment = (commentAuthorId?: string | number): boolean => {
  const videoAuthorId = normalizeUserId(props.videoData.authorId)
  const commentUserId = normalizeUserId(commentAuthorId)
  return !!videoAuthorId && !!commentUserId && videoAuthorId === commentUserId
}

interface CommentGroup {
  root: VideoCommentItem
  replies: VideoCommentItem[]
}

const parseCommentId = (value?: string | number | null): number => {
  const numeric = Number(value ?? 0)
  if (!Number.isFinite(numeric) || numeric <= 0) {
    return 0
  }
  return Math.floor(numeric)
}

const parseCommentTime = (value?: string): number => {
  const timestamp = Date.parse(value || '')
  if (!Number.isFinite(timestamp)) {
    return 0
  }
  return timestamp
}

const formatCommentTime = (value?: string): string => {
  const timestamp = parseCommentTime(value)
  if (!timestamp) {
    return value || ''
  }
  const now = Date.now()
  const diff = now - timestamp
  if (!Number.isFinite(diff) || diff < 0) {
    return value || ''
  }

  const minuteMs = 60 * 1000
  const hourMs = 60 * minuteMs
  const dayMs = 24 * hourMs

  if (diff < minuteMs) {
    return '刚刚'
  }
  if (diff < hourMs) {
    return `${Math.floor(diff / minuteMs)}分钟前`
  }
  if (diff < dayMs) {
    return `${Math.floor(diff / hourMs)}小时前`
  }

  const dayCount = Math.floor(diff / dayMs)
  if (dayCount < 7) {
    return `${dayCount}天前`
  }
  if (dayCount < 30) {
    const weekCount = Math.floor(dayCount / 7)
    return `${Math.max(1, weekCount)}周前`
  }
  if (dayCount < 365) {
    const monthCount = Math.floor(dayCount / 30)
    return `${Math.max(1, monthCount)}个月前`
  }

  const date = new Date(timestamp)
  const year = date.getFullYear()
  const month = `${date.getMonth() + 1}`.padStart(2, '0')
  const day = `${date.getDate()}`.padStart(2, '0')
  return `${year}年${month}月${day}日`
}

const resolveAvatarUrl = (avatar?: string): string => {
  if (!avatar) {
    return ''
  }
  if (/^(https?:)?\/\//.test(avatar) || avatar.startsWith('data:image/')) {
    return avatar
  }
  if (avatar.startsWith('/file/')) {
    return avatar
  }
  const cleaned = avatar.replace(/^\/+/, '')
  if (cleaned.startsWith('file/')) {
    return `/${cleaned}`
  }
  return `/file/${cleaned}`
}

const buildAvatarFallback = (): string => {
  const seedSource = props.videoData.authorId ?? props.videoData.videoId ?? 'author'
  const seed = encodeURIComponent(String(seedSource))
  return `https://picsum.photos/seed/avatar-${seed}/80/80`
}

const buildCommentAvatarFallback = (seedSource?: string | number): string => {
  const seed = encodeURIComponent(String(seedSource || 'comment-author'))
  return `https://picsum.photos/seed/comment-avatar-${seed}/64/64`
}

const resolveCommentAvatar = (avatar?: string, seedSource?: string | number): string => {
  const resolved = resolveAvatarUrl(avatar?.trim())
  return resolved || buildCommentAvatarFallback(seedSource)
}

const authorAvatarUrl = computed(() => {
  const resolved = resolveAvatarUrl(props.videoData.authorAvatar?.trim())
  return resolved || buildAvatarFallback()
})

const handleAvatarLoadError = (event: Event) => {
  const target = event.target as HTMLImageElement | null
  if (!target) {
    return
  }
  target.onerror = null
  target.src = buildAvatarFallback()
}

const openAuthorProfile = () => {
  const userId = normalizeUserId(props.videoData.authorId) || normalizeUserId(props.videoData.author)
  if (!userId) {
    return
  }
  const target = router.resolve({
    path: '/mine',
    query: {
      userId,
      nickName: props.videoData.author || '',
      avatar: props.videoData.authorAvatar || '',
    },
  })
  window.open(target.href, '_blank', 'noopener,noreferrer')
}

const emit = defineEmits(['ready', 'play', 'pause', 'ended', 'error', 'fullscreen-change', 'comment-panel-change'])

const {
  playerContainer,
  videoContainer,
  videoStageStyle,
  progressBar,
  controlBarRef,
  volumeSlider,
  danmuSettingsRef,
  danmuInput,
  isPlaying,
  currentTime,
  totalDuration,
  progressPercent,
  controlsVisible,
  showControls,
  startHideTimer,
  togglePlay,
  showDanmu,
  isDanmuDisabledByInteraction,
  danmuList,
  danmuInputText,
  likeCount,
  commentCount,
  collectCount,
  shareCount,
  showCommentPanel,
  isCommentDisabledByInteraction,
  commentList,
  commentInputText,
  commentReplyTarget,
  commentLoading,
  commentSubmitting,
  commentHasMore,
  isLiked,
  isCollected,
  isShared,
  reactionMutating,
  followActionState,
  followMutating,
  showDanmuSettings,
  danmuFontSize,
  danmuOpacity,
  danmuSpeed,
  danmuArea,
  toggleDanmuSettings,
  closeDanmuSettings,
  updateDanmuSettings,
  seekToPosition,
  playbackRates,
  currentPlaybackRate,
  showPlaybackRateMenu,
  togglePlaybackRateMenu,
  changePlaybackRate,
  sendDanmuFromInput,
  currentQuality,
  sortedQualities,
  changeQuality,
  volume,
  isMuted,
  toggleMute,
  changeVolumeVertical,
  isFullscreen,
  toggleFullscreen,
  loading,
  error,
  retry,
  handleSendDanmu,
  handleLike,
  handleComment,
  handleCommentLike,
  handleCommentDislike,
  isCommentLiked,
  isCommentDisliked,
  getCommentDislikeCount,
  isCommentLikeMutating,
  closeCommentPanel,
  beginReplyComment,
  cancelReplyComment,
  submitComment,
  handleCommentInputKeydown,
  loadMoreComments,
  handleFollow,
  handleShare,
  handleCollect,
  formatTime,
  formatPublishTime,
  exposed,
} = useDouyinVideoPlayer(props, emit)

const REPLY_PAGE_SIZE = 3
const expandedReplies = ref<Record<number, number>>({})

const getExpandedReplyCount = (rootCommentId?: number | string): number => {
  const rootId = parseCommentId(rootCommentId as number | string | null | undefined)
  if (!rootId) {
    return 0
  }
  return expandedReplies.value[rootId] || 0
}

const isReplyExpanded = (rootCommentId?: number | string): boolean => {
  return getExpandedReplyCount(rootCommentId) > 0
}

const toggleReplyExpanded = (rootCommentId: number | string, totalReplies: number) => {
  const rootId = parseCommentId(rootCommentId as number | string | null | undefined)
  if (!rootId || totalReplies <= 0) {
    return
  }
  const current = getExpandedReplyCount(rootId)
  const next = { ...expandedReplies.value }
  if (current > 0) {
    next[rootId] = 0
  } else {
    next[rootId] = Math.min(REPLY_PAGE_SIZE, totalReplies)
  }
  expandedReplies.value = next
}

const expandMoreReplies = (rootCommentId: number | string, totalReplies: number) => {
  const rootId = parseCommentId(rootCommentId as number | string | null | undefined)
  if (!rootId || totalReplies <= 0) {
    return
  }
  const current = getExpandedReplyCount(rootId)
  const next = { ...expandedReplies.value }
  next[rootId] = Math.min(totalReplies, Math.max(REPLY_PAGE_SIZE, current + REPLY_PAGE_SIZE))
  expandedReplies.value = next
}

const collapseReplies = (rootCommentId: number | string) => {
  const rootId = parseCommentId(rootCommentId as number | string | null | undefined)
  if (!rootId) {
    return
  }
  const next = { ...expandedReplies.value }
  next[rootId] = 0
  expandedReplies.value = next
}

const getVisibleReplies = (group: CommentGroup): VideoCommentItem[] => {
  const rootId = parseCommentId(group.root.commentId)
  if (!rootId) {
    return []
  }
  const visibleCount = getExpandedReplyCount(rootId)
  if (visibleCount <= 0) {
    return []
  }
  return group.replies.slice(0, visibleCount)
}

const canReplyExpandMore = (group: CommentGroup): boolean => {
  const rootId = parseCommentId(group.root.commentId)
  if (!rootId) {
    return false
  }
  const visibleCount = getExpandedReplyCount(rootId)
  return visibleCount > 0 && visibleCount < group.replies.length
}

const commentGroups = computed<CommentGroup[]>(() => {
  const source = Array.isArray(commentList.value) ? commentList.value : []
  const byId = new Map<number, VideoCommentItem>()
  source.forEach((item) => {
    const id = parseCommentId(item.commentId)
    if (id > 0) {
      byId.set(id, item)
    }
  })

  const roots: VideoCommentItem[] = []
  const replyMap = new Map<number, VideoCommentItem[]>()

  source.forEach((item) => {
    const parentId = parseCommentId(item.parentCommentId as number | null | undefined)
    if (parentId <= 0 || !byId.has(parentId)) {
      roots.push(item)
      return
    }
    const list = replyMap.get(parentId) || []
    list.push(item)
    replyMap.set(parentId, list)
  })

  roots.sort((a, b) => {
    const timeGap = parseCommentTime(b.createdAt) - parseCommentTime(a.createdAt)
    if (timeGap !== 0) {
      return timeGap
    }
    return parseCommentId(b.commentId) - parseCommentId(a.commentId)
  })

  return roots.map((root) => {
    const rootId = parseCommentId(root.commentId)
    const replies = [...(replyMap.get(rootId) || [])]
    replies.sort((a, b) => {
      const timeGap = parseCommentTime(a.createdAt) - parseCommentTime(b.createdAt)
      if (timeGap !== 0) {
        return timeGap
      }
      return parseCommentId(a.commentId) - parseCommentId(b.commentId)
    })
    return {
      root,
      replies,
    }
  })
})

watch(
  () => commentGroups.value,
  (groups) => {
    const next: Record<number, number> = {}
    groups.forEach((group) => {
      const rootId = parseCommentId(group.root.commentId)
      if (!rootId) {
        return
      }
      const existing = expandedReplies.value[rootId] || 0
      if (existing > 0) {
        next[rootId] = Math.min(existing, group.replies.length)
      }
    })
    expandedReplies.value = next
  },
  { deep: true }
)

defineExpose(exposed)
</script>

<style scoped lang="scss" src="./styles/DouyinVideoPlayer.scss"></style>
