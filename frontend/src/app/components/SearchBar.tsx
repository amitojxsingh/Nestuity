'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';

export default function SearchBar() {
  const [query, setQuery] = useState('');
  const router = useRouter();

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    if (!query.trim()) return;
    const term = query.trim();

    try {
      const historyKey = 'nestuitySearchHistory';
      const countsKey = 'nestuitySearchCounts';

      const historyRaw = typeof window !== 'undefined' ? localStorage.getItem(historyKey) : null;
      const countsRaw = typeof window !== 'undefined' ? localStorage.getItem(countsKey) : null;

      const history: Array<{ term: string; ts: number }> = historyRaw ? JSON.parse(historyRaw) : [];
      const counts: Record<string, number> = countsRaw ? JSON.parse(countsRaw) : {};

      const now = Date.now();
      history.push({ term, ts: now });
      const trimmedHistory = history.slice(-20);

      counts[term] = (counts[term] || 0) + 1;

      localStorage.setItem(historyKey, JSON.stringify(trimmedHistory));
      localStorage.setItem(countsKey, JSON.stringify(counts));
    } catch (err) {
      if (process.env.NODE_ENV === 'development') { // catches error when in development
        // eslint-disable-next-line no-console
        console.warn('localStorage error while saving search history:', err);
      }
    }
    router.push(`/price-comparison?query=${encodeURIComponent(term)}`);
  };

  return (
    <form onSubmit={handleSearch} className="flex justify-center h-15">
      <input
        type="text"
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        placeholder="Search for baby products..."
        className="border border-gray-300 rounded-l-lg p-3 w-full text-gray-900 bg-white focus:outline-none focus:ring-2 focus:ring-[#0543BF]"
      />
      <button
        type="submit"
        className="bg-[#0543BF] text-white text-lg px-6 rounded-r-lg hover:bg-[#6F0094] transition-colors"
      >
        Search
      </button>
    </form>
  );
}