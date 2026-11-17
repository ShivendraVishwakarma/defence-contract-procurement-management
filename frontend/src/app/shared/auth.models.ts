export interface UserProfile {
  id: number;
  username: string;
  displayName: string;
  email: string;
  roles: string[];
}

export interface LoginCredentials {
  username: string;
  password: string;
}

export interface ApiMessage {
  message: string;
}
