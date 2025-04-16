import React from 'react';
import { Link, useLocation } from 'react-router-dom';

const Header = () => {
  const location = useLocation();

  const isActive = (path) => location.pathname === path;

  const navLinks = [
    { to: '/categories', label: '📂 Kategorijas' },
    { to: '/receipts', label: '📄 Visi čeki' },
    { to: '/products', label: '🛒 Produkti' },
    { to: '/add', label: '📥 Pievienot' }
  ];

  return (
    <header className="bg-white shadow">
      <div className="max-w-6xl mx-auto px-4 py-4 flex items-center justify-between">
        <h1 className="text-xl font-bold text-blue-700 flex items-center gap-2">
          💰 Budžeta Lietotne
        </h1>
        <nav className="flex gap-4 text-gray-700 font-medium">
          {navLinks.map(({ to, label }) => (
            <Link
              key={to}
              to={to}
              className={`transition px-3 py-2 rounded-md ${
                isActive(to)
                  ? 'bg-blue-100 text-blue-700 font-semibold'
                  : 'hover:bg-blue-50'
              }`}
            >
              {label}
            </Link>
          ))}
        </nav>
      </div>
    </header>
  );
};

export default Header;
