'use client';

interface FilterSortBarProps {
  productTypeFilter: string;
  retailerFilter: string;
  priceRangeFilter: [number | undefined, number | undefined];
  sortOption: string;
  setProductTypeFilter: (value: string) => void;
  setRetailerFilter: (value: string) => void;
  setPriceRangeFilter: (value: [number | undefined, number | undefined]) => void;
  setSortOption: (value: string) => void;
}

export default function FilterSortBar({
  productTypeFilter,
  retailerFilter,
  priceRangeFilter,
  sortOption,
  setProductTypeFilter,
  setRetailerFilter,
  setPriceRangeFilter,
  setSortOption,
}: FilterSortBarProps) {
  return (
    <div className="flex flex-wrap justify-center gap-4 mb-6 bg-[#AFD1FF] p-4 rounded-lg shadow-sm">
      {/* Product Type */}
      <select
        value={productTypeFilter}
        onChange={(e) => setProductTypeFilter(e.target.value)}
        className="border border-[#B19F44] rounded p-2 shadow-sm focus:outline-none focus:ring-2 focus:ring-[#6F0094] text-black bg-white"
      >
        <option value="All">All Types</option>
        <option value="diaper">Diaper</option>
        <option value="lotion">Lotion</option>
      </select>

      {/* Retailer */}
      <select
        value={retailerFilter}
        onChange={(e) => setRetailerFilter(e.target.value)}
        className="border border-[#B19F44] rounded p-2 shadow-sm focus:outline-none focus:ring-2 focus:ring-[#6F0094] text-black bg-white"
      >
        <option value="All">All Retailers</option>
        <option value="Walmart">Walmart</option>
        <option value="Amazon">Amazon</option>
      </select>

      {/* Price Range */}
      <input
        type="number"
        placeholder="Min Price"
        value={priceRangeFilter[0] ?? ''}
        onChange={(e) =>
          setPriceRangeFilter([e.target.value ? Number(e.target.value) : undefined, priceRangeFilter[1]])
        }
        className="border border-[#B19F44] rounded p-2 w-24 placeholder-gray-600 shadow-sm focus:outline-none focus:ring-2 focus:ring-[#6F0094] text-black bg-white"
      />
      <input
        type="number"
        placeholder="Max Price"
        value={priceRangeFilter[1] ?? ''}
        onChange={(e) =>
          setPriceRangeFilter([priceRangeFilter[0], e.target.value ? Number(e.target.value) : undefined])
        }
        className="border border-[#B19F44] rounded p-2 w-24 placeholder-gray-600 shadow-sm focus:outline-none focus:ring-2 focus:ring-[#6F0094] text-black bg-white"
      />

      {/* Sort */}
      <select
        value={sortOption}
        onChange={(e) => setSortOption(e.target.value)}
        className="border border-[#B19F44] rounded p-2 shadow-sm focus:outline-none focus:ring-2 focus:ring-[#6F0094] text-black bg-white"
      >
        <option value="">Sort By</option>
        <option value="price-asc">Price: Low → High</option>
        <option value="price-desc">Price: High → Low</option>
      </select>
    </div>
  );
}
