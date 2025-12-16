import Link from 'next/link';

type props = {
    storeName: string;
    icon: string,
    price: number,
    pStatus: 'increase' | 'decrease'
};

export default function PriceCard({ storeName, icon, price, pStatus }: props) {
    return(
        <div>
            <div className='grid grid-cols-2 grid-rows-2 gap-1 p-3 bg-primary/50 rounded-[30px] text-2xl font-semibold shadow-md min-w-72'>
                <p className='text-accent-secondary'>{ storeName }</p>
                <p className={`text-right ${
                    pStatus === 'increase' ? 'text-red' : 'text-green'}`}>
                        ${ price }
                </p>
                <img src={ icon } alt='Store icon' className='h-11 rounded-lg'/>
                <Link href='/price-comparison' className='text-center pt-1.5 bg-accent-primary text-white hover:bg-secondary/50 hover:text-accent-primary ease-in-out duration-300 rounded-[20px] shadow-md'>More...</Link>
            </div>
        </div>
    );
}