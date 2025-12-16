'use client';

import { BabyProduct } from '@/types/product.types';
import { useState } from 'react';

interface ProductDetailsCardProps {
  product: BabyProduct;
}

export default function ProductDetailsCard({ product }: ProductDetailsCardProps) {
  const [isExpanded, setIsExpanded] = useState(false);

  // Get unique vendors and their URLs
  const vendorUrls = product.priceHistory.reduce((acc, item) => {
    if (!acc[item.retailer]) {
      acc[item.retailer] = item.productUrl;
    }
    return acc;
  }, {} as Record<string, string>);

  // Truncate description to approximately 150 characters
  const truncateDescription = (text: string, maxLength: number = 150) => {
    if (text.length <= maxLength) return text;
    return text.substring(0, maxLength).trim() + '...';
  };

  const shouldShowToggle = product.description && product.description.length > 150;

  return (
    <div className="bg-highlight rounded-xl border border-border-primary shadow-sm p-3">
      <h3 className="text-base font-bold text-foreground mb-2">{product.name}</h3>

      {product.brand && (
        <p className="text-sm font-medium text-dark-grey mb-2">
          <span className="text-grey">by</span> {product.brand}
        </p>
      )}

      {product.description && (
        <div className="mb-3">
          <p className="text-sm text-dark-grey leading-relaxed">
            {isExpanded ? product.description : truncateDescription(product.description)}
          </p>
          {shouldShowToggle && (
            <button
              onClick={() => setIsExpanded(!isExpanded)}
              className="text-xs text-accent-primary hover:text-accent-secondary font-medium mt-1 transition-colors"
            >
              {isExpanded ? 'See less' : 'See more'}
            </button>
          )}
        </div>
      )}

      {/* Vendor Links */}
      {Object.keys(vendorUrls).length > 0 && (
        <div className="flex flex-wrap gap-2">
          {Object.entries(vendorUrls).map(([vendor, url]) => (
            <a
              key={vendor}
              href={url}
              target="_blank"
              rel="noopener noreferrer"
              className="inline-flex items-center gap-1.5 px-3 py-1.5 bg-accent-primary text-white text-xs font-medium rounded-lg hover:bg-accent-secondary transition-colors"
            >
              <span>{vendor}</span>
              <svg className="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
              </svg>
            </a>
          ))}
        </div>
      )}
    </div>
  );
}
