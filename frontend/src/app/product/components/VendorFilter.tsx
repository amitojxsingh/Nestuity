interface VendorFilterProps {
  vendors: string[];
  selectedVendor: string;
  onSelectVendor: (vendor: string) => void;
}

export default function VendorFilter({
  vendors,
  selectedVendor,
  onSelectVendor,
}: VendorFilterProps) {
  const vendorFilterOptions = ['All', ...vendors.map((v) => v.charAt(0).toUpperCase() + v.slice(1))];

  return (
    <div className="flex space-x-2 overflow-x-auto pb-1">
      {vendorFilterOptions.map((vendor) => (
        <button
          key={vendor}
          onClick={() => onSelectVendor(vendor)}
          className={`px-4 py-2 rounded-lg text-sm font-semibold whitespace-nowrap transition-all duration-200 ${
            selectedVendor === vendor
              ? 'bg-accent-primary text-white shadow-sm'
              : 'bg-light-grey text-dark-grey hover:bg-grey'
          }`}
        >
          {vendor === 'All' ? 'All Vendors' : vendor}
        </button>
      ))}
    </div>
  );
}

