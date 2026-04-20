import React from 'react' // React primitives for functional component definition

/**
 * Interface for the Tag component properties.
 * Defines the behavioral and data requirements for individual tag representations.
 */
interface tagProps {
    name: string; // The text content/label of the tag
    onClick: (e: React.MouseEvent) => void; // Click event handler for navigation or selection
}

/**
 * Reusable Tag Component.
 * Implements the 'Cyber-Minimalist' visual language with high contrast and mono-typography.
 * Designed for micro-interactions within metadata-heavy contexts (e.g. Card, Sidebar).
 */
const Tag: React.FC<tagProps> = ({ name, onClick }) => {
    return (
        <button 
            onClick={onClick} 
            className='px-2 py-0.5 rounded-sm cursor-pointer w-fit h-fit bg-lime-500/10 text-lime-400 border border-lime-500/30 text-[10px] uppercase font-mono font-bold tracking-wider hover:bg-lime-500/20 hover:border-lime-500 hover:shadow-[0_0_10px_rgba(132,204,22,0.4)] transition-all duration-300 active:scale-95'
            aria-label={`Filter by ${name}`}
        >
            {name}
        </button>
    )
}

export default Tag