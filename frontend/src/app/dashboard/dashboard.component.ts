import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../shared/auth.service';
import { UserProfile } from '../shared/auth.models';

@Component({ selector: 'app-dashboard', templateUrl: './dashboard.component.html', styleUrls: ['./dashboard.component.css'] })
export class DashboardComponent implements OnInit {
  profile: UserProfile | null = null;
  get firstName(): string { return this.profile?.displayName.split(' ')[0] || 'there'; }

  constructor(private readonly auth: AuthService, private readonly router: Router) {}

  ngOnInit(): void {
    this.auth.profile$.subscribe(profile => this.profile = profile);
    if (!this.profile) this.auth.currentProfile().subscribe();
  }

  signOut(): void {
    this.auth.logout().subscribe({ next: () => this.router.navigateByUrl('/login'), error: () => this.router.navigateByUrl('/login') });
  }

  roleLabel(role: string): string {
    return role.replace(/^ROLE_/, '').replace(/_/g, ' ').toLowerCase()
      .replace(/\b\w/g, character => character.toUpperCase());
  }
}
