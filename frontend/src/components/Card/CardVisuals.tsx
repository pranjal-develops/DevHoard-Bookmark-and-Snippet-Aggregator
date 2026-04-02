import React from 'react'

interface CardVisualsProp {
    imgUrl: string;
    title: string;
}

const CardVisuals: React.FC<CardVisualsProp> = ({ imgUrl, title }) => {
    return (
        // Image Layer with Scanning Glow
        <div className="h-44 relative bg-zinc-100 dark:bg-zinc-950 border-b border-zinc-100 dark:border-zinc-900 overflow-hidden" >
            <img
                src={imgUrl || "https://images.unsplash.com/photo-1550745165-9bc0b252726f?auto=format&fit=crop&q=80&w=400"}
                alt={title}
                className="w-full h-full object-cover grayscale-20 group-hover:grayscale-0 group-hover:scale-110 transition-all duration-700 opacity-80 group-hover:opacity-100"
            />
            {/* Neon HUD Overlay */}
            <div className="absolute inset-0 bg-linear-to-t from-black/80 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-500 flex items-end p-4">
                <span className="text-[10px] font-mono text-lime-400 font-bold tracking-widest uppercase flex items-center gap-2">
                    <span className="w-1.5 h-1.5 bg-lime-400 rounded-full animate-pulse" />
                    NODE_ACCESS: GRANTED
                </span>
            </div>
        </div>
    )
}

export default CardVisuals