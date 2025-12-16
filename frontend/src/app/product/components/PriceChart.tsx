'use client';

import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

interface PriceChartProps {
  priceData: Array<{ date: string; [key: string]: string | number }>;
  vendors: string[];
  selectedVendor: string;
  paddedMinPrice: number;
  paddedMaxPrice: number;
  paddedPriceRange: number;
}

export default function PriceChart({
  priceData,
  vendors,
  selectedVendor,
  paddedMinPrice,
  paddedMaxPrice,
}: PriceChartProps) {
  // Format date to readable format
  const formatDate = (dateStr: string) => {
    const date = new Date(dateStr);
    const month = date.toLocaleDateString('en-US', { month: 'short' });
    const day = date.getDate();
    const year = date.getFullYear();
    return `${month} ${day}, ${year}`;
  };

  // Format chart data with readable dates
  const chartData = priceData.map(item => ({
    ...item,
    displayDate: formatDate(item.date as string),
  }));

  // Professional color palette using global theme colors
  const colorPalette = [
    '#0543BF',  // celestial-blue
    '#6F0094',  // royal-purple
    '#4CAF50',  // green
    '#F59E0B',  // amber
    '#B194FF',  // lavender
    '#06B6D4',  // cyan
  ];

  // Get active vendors with their colors
  const activeVendors = vendors
    .map((vendor, index) => ({
      vendor,
      name: vendor.charAt(0).toUpperCase() + vendor.slice(1),
      color: colorPalette[index % colorPalette.length],
    }))
    .filter((v) => selectedVendor === 'All' || selectedVendor === v.name);

  // Custom tooltip component
  const CustomTooltip = ({ active, payload, label }: any) => {
    if (active && payload && payload.length) {
      return (
        <div className="bg-white border border-border-primary rounded-lg shadow-lg p-3">
          <p className="text-sm font-semibold text-foreground mb-2">{label}</p>
          {payload.map((entry: any, index: number) => {
            // Find the vendor URL from priceData
            const dataPoint = priceData.find(d => formatDate(d.date as string) === label);
            const vendorUrl = dataPoint?.[`${entry.dataKey}Url`];

            return (
              <div key={index} className="flex items-center justify-between gap-3 mb-1">
                <div className="flex items-center gap-2">
                  <div
                    className="w-2.5 h-2.5 rounded-full"
                    style={{ backgroundColor: entry.color }}
                  />
                  <span className="text-sm font-medium text-dark-grey capitalize">
                    {entry.dataKey}:
                  </span>
                </div>
                <span className="text-sm font-bold text-foreground">
                  ${entry.value?.toFixed(2)}
                </span>
              </div>
            );
          })}
          <p className="text-xs text-dark-grey mt-2 italic">Click point to view product</p>
        </div>
      );
    }
    return null;
  };

  // Handle click on data point
  const handleClick = (data: any) => {
    if (data && data.activePayload && data.activePayload.length > 0) {
      const clickedPoint = data.activePayload[0];
      const vendor = clickedPoint.dataKey;

      // Find the URL for this vendor from the original price data
      const dataPoint = priceData.find(d => formatDate(d.date as string) === data.activeLabel);

      if (dataPoint) {
        const url = dataPoint[`${vendor}Url`];
        if (url && typeof url === 'string') {
          // Open product URL in new tab
          window.open(url, '_blank', 'noopener,noreferrer');
        }
      }
    }
  };

  return (
    <div className="bg-highlight rounded-xl border border-border-primary shadow-sm p-4">
      <ResponsiveContainer width="100%" height={300}>
        <LineChart
          data={chartData}
          onClick={handleClick}
          margin={{ top: 5, right: 10, left: 0, bottom: 5 }}
        >
          <CartesianGrid strokeDasharray="3 3" stroke="#D1D5DB" opacity={0.4} />
          <XAxis
            dataKey="displayDate"
            tick={{ fill: '#4B5563', fontSize: 12 }}
            tickLine={{ stroke: '#D1D5DB' }}
            axisLine={{ stroke: '#D1D5DB' }}
            interval="preserveStartEnd"
          />
          <YAxis
            domain={[Math.floor(paddedMinPrice), Math.ceil(paddedMaxPrice)]}
            tick={{ fill: '#4B5563', fontSize: 12 }}
            tickLine={{ stroke: '#D1D5DB' }}
            axisLine={{ stroke: '#D1D5DB' }}
            tickFormatter={(value) => `$${value.toFixed(2)}`}
          />
          <Tooltip content={<CustomTooltip />} cursor={{ stroke: '#0543BF', strokeWidth: 1 }} />
          {activeVendors.length > 1 && (
            <Legend
              wrapperStyle={{ paddingTop: '10px' }}
              iconType="circle"
              formatter={(value) => <span className="text-sm font-medium text-dark-grey capitalize">{value}</span>}
            />
          )}
          {activeVendors.map(({ vendor, color }) => (
            <Line
              key={vendor}
              type="monotone"
              dataKey={vendor}
              stroke={color}
              strokeWidth={3}
              dot={false}
              activeDot={{ r: 6, cursor: 'pointer', onClick: (e: any, payload: any) => {
                const url = payload.payload[`${vendor}Url`];
                if (url) {
                  window.open(url, '_blank', 'noopener,noreferrer');
                }
              }}}
              name={vendor.charAt(0).toUpperCase() + vendor.slice(1)}
            />
          ))}
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
}
