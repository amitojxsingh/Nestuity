export default function LoadingState() {
  return (
    <div className="min-h-screen bg-gradient-to-b from-gray-50 to-gray-100 flex items-center justify-center" data-cy="loading-state">
        {/* TODO: figure out using a classname for w-20 animate-bounce */}
        <div className="flex flex-col text-center">
            <section className="flex">
                {/* Duck 1 */}
                <img src="\icons\svg\nestuity_icon1_colour.svg" alt="duck" className="w-20 animate-bounce [animation-delay:-0.2s]"/>
                {/* Duck 2 */}
                <img src="\icons\svg\nestuity_icon1_colour.svg" alt="duck" className="w-20 animate-bounce [animation-delay:0s]" />
                {/* Duck 3 */}
                <img src="\icons\svg\nestuity_icon1_colour.svg" alt="duck" className="w-20 animate-bounce [animation-delay:0.2s]" />
            </section>
             <p className="text-black">Loading...</p>
        </div>

    </div>
  );
}

