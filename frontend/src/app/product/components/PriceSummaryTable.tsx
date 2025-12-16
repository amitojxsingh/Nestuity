import { VendorData } from '@/types/product.types';

interface PriceSummaryTableProps {
  vendorSummaries: Map<string, VendorData>;
}

export default function PriceSummaryTable({ vendorSummaries }: PriceSummaryTableProps) {
  // Professional color palette (matching chart colors)
  const colorPalette = [
    { bg: 'bg-indigo-50', text: 'text-indigo-700', dot: '#6366F1' },
    { bg: 'bg-pink-50', text: 'text-pink-700', dot: '#EC4899' },
    { bg: 'bg-emerald-50', text: 'text-emerald-700', dot: '#10B981' },
    { bg: 'bg-amber-50', text: 'text-amber-700', dot: '#F59E0B' },
    { bg: 'bg-violet-50', text: 'text-violet-700', dot: '#8B5CF6' },
    { bg: 'bg-cyan-50', text: 'text-cyan-700', dot: '#06B6D4' },
  ];

  return (
    <div className="bg-white border border-gray-200 rounded-xl shadow-sm overflow-hidden">
      {/* Table Header */}
      <div className="px-4 py-2.5 bg-gray-50 border-b border-gray-200">
        <h3 className="text-sm font-semibold text-gray-900">Price Summary</h3>
      </div>

      {/* Desktop Table View */}
      <div className="hidden md:block overflow-x-auto">
        <table className="w-full">
          <thead>
            <tr className="bg-gray-50 text-xs font-semibold text-gray-600 uppercase tracking-wider border-b border-gray-200">
              <th className="px-4 py-2 text-left">Vendor</th>
              <th className="px-4 py-2 text-left">Current</th>
              <th className="px-4 py-2 text-left">Low</th>
              <th className="px-4 py-2 text-left">High</th>
              <th className="px-4 py-2 text-left">Avg</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {Array.from(vendorSummaries.entries()).map(([vendorKey, vendorData], index) => {
              const colorScheme = colorPalette[index % colorPalette.length];

              return (
                <tr
                  key={vendorKey}
                  className="hover:bg-gray-50 transition-colors duration-150"
                >
                  <td className="px-4 py-2.5">
                    <div className="flex items-center gap-2">
                      <div
                        className="w-2 h-2 rounded-full flex-shrink-0"
                        style={{ backgroundColor: colorScheme.dot }}
                      />
                      <span className="font-semibold text-sm text-gray-900">{vendorData.name}</span>
                    </div>
                  </td>
                  <td className="px-4 py-2.5">
                    {vendorData.isGoodDeal ? (
                      <div className="flex flex-col gap-0.5">
                        <span className="inline-flex items-center gap-1 bg-emerald-50 text-emerald-700 px-2 py-0.5 rounded text-xs font-semibold border border-emerald-200 w-fit">
                          <svg className="w-3 h-3" fill="currentColor" viewBox="0 0 20 20">
                            <path
                              fillRule="evenodd"
                              d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
                              clipRule="evenodd"
                            />
                          </svg>
                          ${vendorData.summary.current.toFixed(2)}
                        </span>
                        <span className="text-xs text-emerald-600 font-medium">Great Deal!</span>
                      </div>
                    ) : (
                      <span className="text-sm font-semibold text-gray-900">
                        ${vendorData.summary.current.toFixed(2)}
                      </span>
                    )}
                  </td>
                  <td className="px-4 py-2.5">
                    <span className="text-sm font-medium text-gray-700">
                      ${vendorData.summary.lowest.toFixed(2)}
                    </span>
                  </td>
                  <td className="px-4 py-2.5">
                    <span className="text-sm font-medium text-gray-700">
                      ${vendorData.summary.highest.toFixed(2)}
                    </span>
                  </td>
                  <td className="px-4 py-2.5">
                    <span className="text-sm font-medium text-gray-700">
                      ${vendorData.summary.average.toFixed(2)}
                    </span>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>

      {/* Mobile Card View */}
      <div className="md:hidden divide-y divide-gray-100">
        {Array.from(vendorSummaries.entries()).map(([vendorKey, vendorData], index) => {
          const colorScheme = colorPalette[index % colorPalette.length];

          return (
            <div key={vendorKey} className="p-3 hover:bg-gray-50 transition-colors">
              {/* Vendor Name */}
              <div className="flex items-center gap-2 mb-2">
                <div
                  className="w-2 h-2 rounded-full flex-shrink-0"
                  style={{ backgroundColor: colorScheme.dot }}
                />
                <span className="font-semibold text-sm text-gray-900">{vendorData.name}</span>
                {vendorData.isGoodDeal && (
                  <span className="ml-auto inline-flex items-center gap-1 bg-emerald-50 text-emerald-700 px-1.5 py-0.5 rounded text-xs font-semibold border border-emerald-200">
                    <svg className="w-2.5 h-2.5" fill="currentColor" viewBox="0 0 20 20">
                      <path
                        fillRule="evenodd"
                        d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
                        clipRule="evenodd"
                      />
                    </svg>
                    Great Deal!
                  </span>
                )}
              </div>

              {/* Price Grid */}
              <div className="grid grid-cols-2 gap-x-4 gap-y-1.5 text-xs">
                <div>
                  <span className="text-gray-500">Current:</span>
                  <span className="ml-1 font-semibold text-gray-900">
                    ${vendorData.summary.current.toFixed(2)}
                  </span>
                </div>
                <div>
                  <span className="text-gray-500">Low:</span>
                  <span className="ml-1 font-medium text-gray-700">
                    ${vendorData.summary.lowest.toFixed(2)}
                  </span>
                </div>
                <div>
                  <span className="text-gray-500">High:</span>
                  <span className="ml-1 font-medium text-gray-700">
                    ${vendorData.summary.highest.toFixed(2)}
                  </span>
                </div>
                <div>
                  <span className="text-gray-500">Avg:</span>
                  <span className="ml-1 font-medium text-gray-700">
                    ${vendorData.summary.average.toFixed(2)}
                  </span>
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}

