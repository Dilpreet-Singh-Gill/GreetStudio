import React from 'react';
import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { describe, it, expect } from 'vitest';
import Dashboard from './Dashboard';

describe('Dashboard Component', () => {
  it('renders the overview header', () => {
    render(
      <BrowserRouter>
        <Dashboard />
      </BrowserRouter>
    );
    expect(screen.getByText('Overview')).toBeInTheDocument();
    expect(screen.getByText("Here's what's happening today")).toBeInTheDocument();
  });

  it('renders all stat cards with mock data', () => {
    render(
      <BrowserRouter>
        <Dashboard />
      </BrowserRouter>
    );
    expect(screen.getByText('Total People')).toBeInTheDocument();
    expect(screen.getByText('1,248')).toBeInTheDocument();
    
    expect(screen.getByText('Birthdays Today')).toBeInTheDocument();
    expect(screen.getByText('12')).toBeInTheDocument();
    
    expect(screen.getByText('Active Templates')).toBeInTheDocument();
    expect(screen.getByText('8')).toBeInTheDocument();
    
    expect(screen.getByText('Posters Generated')).toBeInTheDocument();
    expect(screen.getByText('4,821')).toBeInTheDocument();
  });
});
