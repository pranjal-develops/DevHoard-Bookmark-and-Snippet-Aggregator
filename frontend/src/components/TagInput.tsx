import React, { useState } from 'react' // Hooks for local input state management
import Tag from './Tag'; // Reusable atomic tag component

/**
 * Base properties for the TagInput orchestration.
 * Note from development transition: Changing interface to type to facilitate discriminated union derivation.
 */
type TagInputPropsBase = {     
    tags: string[]; // Current collection of category strings
    setTags: (tags: string[]) => void; // Callback hook for state synchronization
    placeHolder?: string; // Optional instructional text for the search input
    inForm?: boolean; // Flag indicating if the component is rendered within the primary URL form
    isEditing?: boolean; // Flag indicating if the component is in a 'Mutation-allowed' mode (Card context)
    onTagClick?: (tag: string) => void; // Optional interaction handler for non-editing views
}

/**
 * Sub-type for usage within established creation forms.
 * Enforces that onClick behavior is disabled in favor of deletion logic.
 */
type OnURL = TagInputPropsBase & {
    inForm: true;
    onTagClick?: undefined;
}

/**
 * Sub-type for usage within existing record cards.
 * Requires a click handler for navigation while allowing conditional editing.
 */
type NotOnURL = TagInputPropsBase & {
    inForm?: false;
    onTagClick: (tag: string) => void;
}

// Discriminated Union to ensure compile-time safety for different component contexts
type TagInputProps = OnURL | NotOnURL;

/**
 * TagInput Component.
 * Orchestrates the management of dynamic tag collections.
 * Supports both creation (via input) and modification (via deletion) lifecycles.
 */
const TagInput: React.FC<TagInputProps> = ({ tags, setTags, placeHolder, inForm, isEditing, onTagClick }) => {

    /* Local state for the current uncommitted tag string */
    const [value, setValue] = useState('');

    /* 
     * Technical Optimization: 
     * Utilizing a Set for O(1) existence checks. This prevents redundant entries 
     * without iterating through the entire 'tags' array in the render loop.
     */
    const set = new Set(tags);

    return (
        <div className='flex flex-wrap items-center gap-2'
            /* 
             * Event Guard: 
             * Stopping propagation to prevent triggers on parent Card components (e.g. navigation) 
             * when interacting with the input or tags.
             */
            onClick={(e) => e.stopPropagation()}
        >
            {/* Iterative rendering of existing tags with context-aware click handlers */}
            {tags.map((tag, index) => (
                <Tag 
                    key={index} 
                    name={tag} 
                    onClick={(isEditing || inForm) 
                        ? () => setTags(tags.filter((_, i) => i !== index)) 
                        : () => onTagClick!(tag)
                    } 
                />
            ))}

            {/* Conditional Input Rendering: Only available when creating or explicitly editing */}
            {(isEditing || inForm) &&
                <input 
                    value={value} 
                    onChange={(e) => setValue(e.target.value)} 
                    type="text" 
                    placeholder={placeHolder} 
                    className={
                        inForm ?
                            'px-5 py-4 rounded-xl bg-white dark:bg-[hsl(0,0%,10%)] border border-zinc-300 dark:border-zinc-700 focus:outline-none focus:ring-4 focus:ring-lime-500/20 focus:border-lime-500 dark:focus:border-lime-400 transition-all font-mono text-sm'
                            :
                            'flex-1 px-2 py-1 rounded-lg bg-zinc-100 dark:bg-black border border-zinc-200 dark:border-zinc-800 focus:outline-none focus:ring-2 focus:ring-lime-500/20 focus:border-lime-500 dark:focus:border-lime-400 transition-all font-mono text-xs dark:text-zinc-300'
                    }
                    onKeyDown={(e) => {
                        /* Action triggers: 'Enter' and ',' commit the current string to the collection */
                        if (e.key === 'Enter' || e.key === ',') {
                            e.preventDefault();
                            if (value.trim() !== '' && !set.has(value.trim())) {
                                setTags([...tags, value.trim()]); // State projection: Appending a fresh, unique entry
                                setValue(''); // Input Reset
                            }
                        }
                    }} 
                />
            }
        </div>
    )
}

export default TagInput