import React from 'react' // React for functional component definition

/**
 * Interface definition for the CardVisuals component.
 * Manages the visual representation layer of a bookmark, including thumbnail ingestion.
 */
interface CardVisualsProp {
    imgUrl: string; // The URL for the scraped resource thumbnail
    title: string; // The descriptive title used for accessibility (alt-text)
}

/**
 * CardVisuals Component.
 * Orchestrates the 'Image Layer' of a bookmark card.
 * Implements a dynamic 'Scanning' aesthetic through CSS-based transitions and HUD overlays.
 */
const CardVisuals: React.FC<CardVisualsProp> = ({ imgUrl, title }) => {
    return (
        /* 
         * Structural Image Layer: 
         * Designed with overflow-hidden to facilitate the internal zooming/scaling effect 
         * without affecting the card's external dimensions.
         */
        <div className="h-44 relative bg-zinc-100 dark:bg-zinc-950 border-b border-zinc-100 dark:border-zinc-900 overflow-hidden" >

            {/* 
             * Primary Resource Image:
             * Logic Overview:
             * 1. Implements a fallback strategy using a high-quality technology-themed placeholder.
             * 2. Utilizes transition effects (Grayscale -> Color and Scale(1.1)) triggered by parent hover.
             */
            }
            <img
                src={imgUrl || "https://images.unsplash.com/photo-1550745165-9bc0b252726f?auto=format&fit=crop&q=80&w=400"}
                alt={title}
                className="w-full h-full object-cover grayscale-20 group-hover:grayscale-0 group-hover:scale-110 transition-all duration-700 opacity-80 group-hover:opacity-100"
            />

            {/* 
             * Neon HUD (Heads-Up Display) Overlay:
             * Provides thematic status feedback ("NODE_ACCESS: GRANTED") upon component focus.
             * Utilizes a linear gradient for visual contrast against heterogeneous image backgrounds.
             */
            }
            <div className="absolute inset-0 bg-linear-to-t from-black/80 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-500 flex items-end p-4">
                <span className="text-[10px] font-mono text-lime-400 font-bold tracking-widest uppercase flex items-center gap-2">
                    {/* Visual Status Beacon: Pulsing element signifying an active 'Access' state. */}
                    <span className="w-1.5 h-1.5 bg-lime-400 rounded-full animate-pulse" />
                    NODE_ACCESS: GRANTED
                </span>
            </div>
        </div>
    )
}

export default CardVisuals