import React from 'react';
import { mount } from 'cypress/react';
import Header from '@/components/Header/Header';

describe('<Header />', () => {
    it('renders the header and children content', () => {
        const childText = "Hello World";

        mount(
            <Header>
                <span>{childText}</span>
            </Header>
        );

        // Check containers exist
        cy.get('.section-header.layout').should('exist');
        cy.get('.header-container').should('exist');
        cy.get('header.header').should('exist');

        // Ensure children render
        cy.contains(childText).should('be.visible');
    });

    it('allows rendering multiple children', () => {
        mount(
            <Header>
                <h1>Title</h1>
                <p>Subtitle</p>
            </Header>
        );

        cy.get('header.header').within(() => {
            cy.contains('Title').should('be.visible');
            cy.contains('Subtitle').should('be.visible');
        });
    });
});
