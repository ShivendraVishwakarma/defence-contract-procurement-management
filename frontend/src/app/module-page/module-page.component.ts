import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-module-page',
  template: '<main class="module-wrap"><a routerLink="/dashboard" class="back">← Back to overview</a><p class="eyebrow">PROJECT J14 · MODULE NAVIGATION</p><h1>{{ title }}</h1><section class="notice"><span>◷</span><div><strong>This workspace is planned for a later approved story.</strong><p>Its scope and sprint allocation will follow the official companion workbook.</p></div></section></main>',
  styles: [`.module-wrap { width: min(860px, 100%); margin: 0 auto; padding: 64px 28px; } .back { display: inline-block; margin-bottom: 50px; color: #39776c; font-size: 12px; font-weight: 700; } .eyebrow { color: #56887e; font-size: 9px; font-weight: 700; letter-spacing: .17em; } h1 { color: #17353b; font: 700 34px 'Manrope', sans-serif; } .notice { display: flex; gap: 15px; margin-top: 30px; padding: 22px; border: 1px solid #dfe8e3; border-radius: 10px; background: white; color: #35554f; } .notice > span { font-size: 20px; color: #56887e; } .notice p { margin: 8px 0 0; color: #718481; font-size: 12px; }`]
})
export class ModulePageComponent implements OnInit {
  title = '';
  constructor(private readonly route: ActivatedRoute) {}
  ngOnInit(): void { this.title = this.route.snapshot.data['title']; }
}
