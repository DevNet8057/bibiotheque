import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UserAuthService {

  constructor() { }

  public setRoles(roles: Array<{ roleName: string }>) {
    localStorage.setItem('roles', JSON.stringify(roles));
  }

  public getRoles(): Array<{ roleName: string }> {
    const roles = localStorage.getItem('roles');
    return roles ? JSON.parse(roles) : [];
  }

  public setToken(jwtToken: string) {
    localStorage.setItem('jwtToken', jwtToken);
  }

  public getToken(): string | null {
    return localStorage.getItem('jwtToken');
  }

  public setUserId(userId: number) {
    localStorage.setItem('userId', JSON.stringify(userId));
  }

  public getUserId() {
    const userId = localStorage.getItem('userId');
    return userId ? JSON.parse(userId) : null;
  }

  public setName(name: string) {
    localStorage.setItem('name', JSON.stringify(name));
  }

  public getName(): string {
    const name = localStorage.getItem('name');
    return name ? JSON.parse(name) : '';
  }

  public clear() {
    localStorage.removeItem('roles');
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('userId');
    localStorage.removeItem('name');
  }

  public isLoggedIn(): boolean {
    return this.getRoles().length > 0 && !!this.getToken();
  }

}
