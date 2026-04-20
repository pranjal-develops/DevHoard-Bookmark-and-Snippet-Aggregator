import React from 'react' // React for functional component definition

/**
 * Interface definition for CardAction properties.
 * Orchestrates the relationship between individual bookmark identity and view-layer interaction callbacks.
 */
interface CardActionProps {
    id: string; // Unique identifier for the target bookmark entity
    originalUrl: string; // The source URL for display and navigation
    onToggleFavorite: () => void; // Callback for favorite state mutation
    isFavorite: boolean; // Current favorite status for visual rendering
    onDelete: (id: string) => void; // Callback for entity destruction
}

/**
 * CardAction Component.
 * Encapsulates the interaction layer of a bookmark card, including status toggles and deletion triggers.
 * Implements 'Action-on-Hover' patterns to maintain a clean default visual state.
 */
const CardActions: React.FC<CardActionProps> = ({ id, originalUrl, onToggleFavorite, isFavorite, onDelete }) => {
    return (
        /* 
         * Container: Provides a structural separator between metadata and interactive elements.
         * Implementation uses a top-border based demarcation strategy.
         */
        <div className="flex items-center justify-between pt-3 border-t border-zinc-100 dark:border-zinc-900/50">

            {/* Source Display: Renders a truncated version of the origin URL for context. */}
            <div className="flex flex-col">
                <span className="text-[8px] font-mono uppercase text-zinc-400 dark:text-zinc-600 tracking-tighter">SOURCE_NODE_ORIGIN</span>
                <div className="text-[10px] text-zinc-400 dark:text-lime-500/80 font-mono truncate max-w-[140px] lowercase italic" title={originalUrl}>
                    {originalUrl?.replace(/^https?:\/\//, '')}
                </div>
            </div>

            {/*
            * Deletion Trigger (Purge Node).
            *
            * Technical Implementation:
            * Utilizing e.stopPropagation() is critical here to prevent the event from bubbling up
            * to the parent Card container, which would otherwise trigger a navigation to the source URL.
            *
            * Visual Logic:
            * Opacity is tied to 'group-hover', ensuring the 'Trash' icon only appears upon user focus.
            */}
            <button
                onClick={(e) => {
                    e.stopPropagation(); // Preventing redirection trigger on parent card
                    onDelete(id);
                }}
                className="p-2 rounded-md text-zinc-300 dark:text-zinc-800 hover:bg-red-500/10 hover:text-red-500 dark:hover:text-red-400 opacity-0 group-hover:opacity-100 translate-y-2 group-hover:translate-y-0 transition-all duration-300 active:scale-90"
                aria-label="Destroy Bookmark Record"
            >
                <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"><path d="M3 6h18" /><path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6" /><path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2" /><line x1="10" y1="11" x2="10" y2="17" /><line x1="14" y1="11" x2="14" y2="17" /></svg>
            </button>

            {/*
            * Favorite Status Orchestrator.
            *
            * Logic Overview:
            * Positioned absolutely within the card layout to maintain a consistent visual 'Anchor'.
            * Utilizes an 'animate-pulse' effect for active favorites to signify high-importance status.
            */ }
            <button
                onClick={(e) => {
                    e.stopPropagation(); // Preventing redirection trigger on parent card
                    onToggleFavorite();
                }}
                className={`absolute top-4 right-4 p-2.5 rounded-xl transition-all duration-300 z-20 group/heart
                        ${isFavorite
                        ? ' text-red-500'
                        : 'text-black/40 dark:text-white/40 hover:text-white'}`}
            >
                <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="18" height="18"
                    viewBox="0 0 24 24"
                    fill={isFavorite ? "currentColor" : "none"}
                    stroke="currentColor"
                    strokeWidth="2.5"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    className={`${isFavorite ? 'animate-pulse' : 'scale-90 group-heart:scale-110'} transition-transform`}
                >
                    <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l8.84-8.84 1.06-1.06a5.5 5.5 0 0 0 0-7.78z" />
                </svg>
            </button>

            {/* Decorative Geometry: Enhances the 'Cyber-Minimalist' aesthetic through accented corner highlights. */}
            <div className="absolute top-0 right-0 w-8 h-8 pointer-events-none">
                <div className="absolute top-0 right-0 border-t-2 border-r-2 border-transparent group-hover:border-lime-500/30 transition-all duration-500 w-2 h-2"></div>
            </div>
        </div >
    )
}

export default CardActions
