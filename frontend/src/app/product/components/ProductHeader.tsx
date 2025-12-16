'use client';

import { BabyProduct } from '@/types/product.types';
import { useRouter } from 'next/navigation';

interface ProductHeaderProps {
  product: BabyProduct;
}

export default function ProductHeader({ product }: ProductHeaderProps) {
  const router = useRouter();

  return (
    <div className="bg-background border-b border-border-primary shadow-sm mb-4">
      {/* Top Navigation */}
      <div className="flex items-center justify-between px-4 py-3">
        <button
          onClick={() => router.push('/dashboard')}
          className="text-dark-grey hover:text-foreground transition-colors p-1 -ml-1 rounded-lg hover:bg-light-grey"
        >
          <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
          </svg>
        </button>
        <h1 className="text-base font-bold text-foreground">Price Tracker</h1>
        <div className="w-5"></div>
      </div>
    </div>
  );
}

