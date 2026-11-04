import { TestBed } from '@angular/core/testing';
import { LOCALE_ID } from '@angular/core';
import { formatDate } from '@angular/common';
import { appConfig } from './app.config';

describe('Idioma de la aplicación', () => {
  it('configura las fechas en español de México', () => {
    TestBed.configureTestingModule({ providers: appConfig.providers });
    expect(TestBed.inject(LOCALE_ID)).toBe('es-MX');
    expect(formatDate('2026-11-03T12:00:00', 'MMMM', 'es-MX')).toBe('noviembre');
  });
});
