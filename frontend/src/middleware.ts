import { auth } from "@/auth"
import { NextResponse } from "next/server"

export default auth((req) => {
    const { pathname } = req.nextUrl

    // Allow all static files and public assets
    if (
        pathname === '/' ||
        pathname.startsWith('/_next/') ||
        pathname.startsWith('/favicon.ico') ||
        pathname.startsWith('/icons/') ||
        pathname.startsWith('/images/') ||
        pathname.startsWith('/logo') ||
        pathname.startsWith('/other/') ||
        pathname === '/auth/login' ||
        pathname === '/auth/register' ||
        pathname.startsWith('/api/')
    ) {
        return NextResponse.next()
    }

    // Require authentication for all other routes
    if (!req.auth) {
        const newUrl = new URL('/auth/login', req.nextUrl.origin)
        newUrl.searchParams.set('callbackUrl', req.nextUrl.href)
        return NextResponse.redirect(newUrl)
    }

    return NextResponse.next()
})

export const config = {
    matcher: ['/((?!api|_next/static|_next/image|favicon.ico).*)'],
};
