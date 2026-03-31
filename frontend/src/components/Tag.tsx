import React from 'react'

interface tagProps {
    name: string;
    onClick: () => void;
}

const Tag: React.FC<tagProps> = ({ name, onClick }) => {
    return (
        <button onClick={onClick} className='px-2 py-0.5 rounded-sm cursor-pointer w-fit h-fit bg-lime-500/10 text-lime-400 border border-lime-500/30 text-[10px] uppercase font-mono font-bold tracking-wider hover:bg-lime-500/20 hover:border-lime-500 hover:shadow-[0_0_10px_rgba(132,204,22,0.4)] transition-all duration-300 active:scale-95'>{name}</button>
    )
}

export default Tag