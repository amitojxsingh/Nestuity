import { TimeRange } from '@/types/product.types';

interface TimeRangeFilterProps {
  timeRanges: TimeRange[];
  selectedTimeRange: string;
  onSelectTimeRange: (range: string) => void;
}

export default function TimeRangeFilter({
  timeRanges,
  selectedTimeRange,
  onSelectTimeRange,
}: TimeRangeFilterProps) {
  return (
    <div className="px-4 mb-6">
      <div className="inline-flex bg-gray-100 rounded-lg p-1 gap-1">
        {timeRanges.map((range) => (
          <button
            key={range.value}
            onClick={() => onSelectTimeRange(range.label)}
            className={`px-5 py-2 rounded-md text-sm font-semibold transition-all duration-200 ${
              selectedTimeRange === range.label
                ? 'bg-white text-gray-900 shadow-sm'
                : 'text-gray-600 hover:text-gray-900'
            }`}
          >
            {range.label}
          </button>
        ))}
      </div>
    </div>
  );
}

