'use client';

export default function Header({ children }: { children: React.ReactNode }) {
    return (
        <div className="section-header layout">
            <div className="header-container">
                <header className="header">
                    { children }
                </header>
            </div>
        </div>
    );
}
