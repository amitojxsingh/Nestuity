'use client'

import { signOut } from "next-auth/react"
import { useRouter } from "next/navigation"
import Image from "next/image"
import Link from "next/link"
import { useState, useRef, useEffect } from "react"

export default function SettingsDropdown() {
  const [isOpen, setOpen] = useState(false)
  const containerRef = useRef<HTMLDivElement>(null)
  const router = useRouter()

  // Close dropdown when clicking outside
  useEffect(() => {
    function handleClickOutside(event: MouseEvent) {
      if (containerRef.current && !containerRef.current.contains(event.target as Node)) {
        setOpen(false)
      }
    }
    document.addEventListener("mousedown", handleClickOutside)
    return () => document.removeEventListener("mousedown", handleClickOutside)
  }, [])

  async function handleLogout() {
    await signOut({ redirectTo: "/auth/login" })
  }

  return (
    <div ref={containerRef} className="relative">
      <div className="icon-container">
        <button
          onClick={() => setOpen((prev) => !prev)}
          className="p-1 rounded-full hover:bg-gray-100 transition-colors"
        >
          <Image
            src="/icons/svg/profile.svg"
            alt="Settings"
            width={20}
            height={20}
            className="cursor-pointer"
          />
        </button>
      </div>

      {/* Dropdown menu */}
      <div
        className={`
          absolute right-0 top-full mt-2 w-48 bg-white rounded-xl shadow-lg ring-1 ring-black/5 z-50
          transition-all duration-300 ease-in-out
          ${isOpen ? 'opacity-100 translate-y-0 pointer-events-auto' : 'opacity-0 -translate-y-2 pointer-events-none'}
        `}
      >
        <nav className="flex flex-col py-2 px-3">
          <Link
            href="/settings"
            onClick={() => setOpen(false)}
            className="py-1 text-blue-600 hover:underline hover:text-blue-700 transition-colors font-semibold no-underline text-left"
          >
            Manage profile
          </Link>

          <div className="border-t border-gray-200 my-2"></div>

          <button
            onClick={handleLogout}
            className="py-1 text-blue-600 hover:underline hover:text-blue-700 transition-colors font-semibold no-underline text-left"
          >
            Log out
          </button>
        </nav>
      </div>
    </div>
  )
}
