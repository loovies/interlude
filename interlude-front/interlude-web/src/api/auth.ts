import http from './http'
import type { ResponseVO } from './types'
import { unwrapResponse } from './types'
import type { TokenUserInfo } from '@/types/auth'

export interface CheckCodeResponse {
  code: string
  codeKey: string
  checkCodeKey?: string
}

export interface LoginRequestPayload {
  account: string
  password: string
  checkCodeKey: string
  checkCode: string
}

export interface SendEmailCodePayload {
  email: string
  checkCodeKey: string
  checkCode: string
}

export interface RegisterRequestPayload {
  email: string
  nickName: string
  password: string
  emailCode: string
}

export const fetchCheckCode = async (): Promise<CheckCodeResponse> => {
  const res = await http.get<ResponseVO<CheckCodeResponse>>('/account/checkCode')
  return unwrapResponse(res)
}

export const loginByAccount = async (payload: LoginRequestPayload): Promise<TokenUserInfo> => {
  const res = await http.post<ResponseVO<TokenUserInfo>>('/account/login', payload)
  return unwrapResponse(res)
}

export const requestAutoLogin = async (): Promise<TokenUserInfo | null> => {
  const res = await http.get<ResponseVO<TokenUserInfo | null>>('/account/autoLogin')
  return unwrapResponse(res)
}

export const logoutFromServer = async (): Promise<void> => {
  const res = await http.post<ResponseVO<string>>('/account/logout')
  unwrapResponse(res)
}

export const sendRegisterEmailCode = async (payload: SendEmailCodePayload): Promise<string> => {
  const res = await http.post<ResponseVO<string>>('/account/sendEmailCode', payload)
  return unwrapResponse(res)
}

export const registerAccount = async (payload: RegisterRequestPayload): Promise<TokenUserInfo> => {
  const res = await http.post<ResponseVO<TokenUserInfo>>('/account/register', payload)
  return unwrapResponse(res)
}
